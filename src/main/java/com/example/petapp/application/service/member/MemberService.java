package com.example.petapp.application.service.member;

import com.example.petapp.application.in.email.EmailUseCase;
import com.example.petapp.application.in.fcm.FcmUseCase;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.member.mapper.MemberMapper;
import com.example.petapp.application.in.member.object.MemberCreateEvent;
import com.example.petapp.application.in.member.object.MemberDeleteEvent;
import com.example.petapp.application.in.member.object.MemberUpdateEvent;
import com.example.petapp.application.in.member.object.dto.request.*;
import com.example.petapp.application.in.member.object.dto.response.*;
import com.example.petapp.application.in.role.RoleQueryUseCase;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.application.out.MemberSearchPort;
import com.example.petapp.application.out.StoragePort;
import com.example.petapp.application.out.cache.MemberRecentViewCachePort;
import com.example.petapp.application.out.cache.MemberSearchCachePort;
import com.example.petapp.domain.file.FileKind;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.member.model.MemberRole;
import com.example.petapp.domain.role.Role;
import com.example.petapp.interfaces.exception.ConflictException;
import com.example.petapp.interfaces.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberQueryUseCase memberQueryUseCase;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUseCase tokenUseCase;
    private final EmailUseCase emailUseCase;
    private final FcmUseCase fcmUseCase;
    private final RoleQueryUseCase roleQueryUseCase;
    private final StoragePort storagePort;
    private final MemberSearchPort memberSearchPort;
    private final MemberSearchCachePort memberSearchCachePort;
    private final MemberRecentViewCachePort memberRecentViewCachePort;

    private final ApplicationEventPublisher eventPublisher;
    private final View view;

    @Transactional
    @Override
    public MemberSignResponseDto create(MemberSignDto memberSignDto) {
        if (memberRepository.exist(memberSignDto.getEmail())) {
            throw new ConflictException("이미 가입된 회원입니다.");
        }
        String imageFileName = storagePort.uploadFile(memberSignDto.getMemberImageUrl(), FileKind.MEMBER);
        Member member = MemberMapper.toEntity(memberSignDto, passwordEncoder.encode(memberSignDto.getPassword()), imageFileName);
        Member savedMember = memberRepository.save(member);

        //elasticsearch 문서 저장 이벤트 발생
        eventPublisher.publishEvent(new MemberCreateEvent(
                savedMember.getId(),
                savedMember.getName(),
                savedMember.getMemberImageUrl()
        ));
        return new MemberSignResponseDto(savedMember.getId());
    }

    @Transactional
    @Override
    public LoginResponseDto login(LoginDto loginDto, HttpServletResponse response) {
        Member member = memberQueryUseCase.findOrThrow(loginDto.getEmail());
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new UnAuthorizedException("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        Role role = roleQueryUseCase.findUserRole();
        setRole(member, role);
        return tokenUseCase.save(member, role);
    }

    /**
     * 인증코드 검증 후 비밀번호 재설정용 임시 JWT 발급
     */
    @Override
    public AccessTokenResponseDto verifyCode(AuthCodeDto authCodeDto) {//sendEmail할 때 이메일 유효성 검사 했으므로 안해줘도 됨.
        emailUseCase.verifyCode(authCodeDto.getEmail(), authCodeDto.getCode());//todo : name을 email로?
        Member member = memberQueryUseCase.findOrThrow(authCodeDto.getEmail());
        return tokenUseCase.createResetPasswordJwt(member);
    }

    @Override
    public FindByIdResponseDto findById(String phoneNumber) {
        Member member = memberQueryUseCase.findOrThrowByPhoneNumber(phoneNumber);
        return new FindByIdResponseDto(member.getEmail());
    }

    @Override
    public void sendEmail(SendEmailDto sendEmailDto) {
        Member member = memberQueryUseCase.findOrThrow(sendEmailDto.getEmail());
        emailUseCase.send(member.getEmail());
    }


    @Override
    public void logout(String accessToken) {
        tokenUseCase.delete(accessToken);
    }


    @Transactional
    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        if (member.isSamePassword(passwordEncoder, resetPasswordDto.getNewPassword())) {
            throw new IllegalArgumentException("전 비밀번호와 다르게 설정해야합니다.");
        } else {
            member.updatePassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        }
    }

    @Override
    public GetMemberResponseDto get(Long targetId, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);

        memberRecentViewCachePort.create(memberId, targetId); // 최근 본 회원 캐시에 저장

        return MemberMapper.toGetMemberResponseDto(member);
    }

    @Override
    @Transactional
    public void update(UpdateMemberRequestDto requestDto, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        String imageFileName = storagePort.uploadFile(requestDto.getMemberImageUrl(), FileKind.MEMBER);

        member.setName(requestDto.getName());
        member.setMemberImageUrl(imageFileName);

        eventPublisher.publishEvent(new MemberUpdateEvent(memberId, requestDto.getName(), imageFileName));
    }

    @Transactional
    @Override
    public void delete(Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        memberRepository.delete(member);

        eventPublisher.publishEvent(new MemberDeleteEvent(memberId));
    }

    @Transactional
    @Override
    public void createFcmToken(FcmTokenDto fcmTokenDto) {
        Member member = memberQueryUseCase.findOrThrow(fcmTokenDto.getMemberId());
        fcmUseCase.createFcmToken(member, fcmTokenDto.getFcmToken());
    }

    @Override
    public List<MemberSearchResponseDto> autoComplete(String keyword, Long memberId) {
        if (keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("키워드를 입력해주세요.");
        }
        /**
         * 사실 matchQuery만 사용할 때는 필요없으나 termQuery 때문에 핸들 거치고 검색요청해야함.
         */
        String key = keyword.replaceAll("\\s+", "").toLowerCase();
        List<MemberSearchResponseDto> result = memberSearchCachePort.get(key);

        if (result == null) {
            result = memberSearchPort.autoComplete(key);// 캐시 미스면 db에서 조회
            memberSearchCachePort.create(key, result);//해당 자동완성에 캐싱

        }
        if (result == null || result.isEmpty()) return result;

        List<Long> viewList = memberRecentViewCachePort.findList(memberId);
        if (viewList == null || viewList.isEmpty()) return result; //최근 본 회원이 없으면 바로 반환

        Map<Long, MemberSearchResponseDto> map = new HashMap<>(result.size());
        for (MemberSearchResponseDto dto : result) {
            map.put(dto.getMemberId(), dto);
        }

        List<MemberSearchResponseDto> list = new ArrayList<>(result.size());
        Set<Long> picked = new HashSet<>();

        //최근 본 회원을 앞으로 정렬
        for (Long id : viewList) {
            MemberSearchResponseDto dto = map.get(id);
            if (dto != null && picked.add(id)) list.add(dto);
        }

        //나머지는 엘라스틱서치에서 정렬된 순서대로 추가
        for (MemberSearchResponseDto dto : result) {
            if (picked.add(dto.getMemberId())) list.add(dto);
        }

        return list;
    }

    @Override
    public List<MemberSearchResponseDto> search(String keyword, int page, Long memberId) {
        if (keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("키워드를 입력해주세요.");
        }
        return memberSearchPort.search(keyword, page);
    }

    private void setRole(Member member, Role role) {
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();
        member.addRole(memberRole);
    }
}
