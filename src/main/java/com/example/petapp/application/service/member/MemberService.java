package com.example.petapp.application.service.member;

import com.example.petapp.application.common.NameChosungUtil;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.member.mapper.MemberMapper;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.application.in.member.object.MethodType;
import com.example.petapp.application.in.member.object.dto.request.MemberSignDto;
import com.example.petapp.application.in.member.object.dto.request.ResetPasswordDto;
import com.example.petapp.application.in.member.object.dto.request.UpdateMemberRequestDto;
import com.example.petapp.application.in.member.object.dto.response.FindByIdResponseDto;
import com.example.petapp.application.in.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSignResponseDto;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.application.out.MemberSearchPort;
import com.example.petapp.application.out.StoragePort;
import com.example.petapp.application.out.cache.MemberRecentViewCachePort;
import com.example.petapp.application.out.cache.MemberSearchCachePort;
import com.example.petapp.application.out.cache.MemberSearchSuggestionsCachePort;
import com.example.petapp.domain.file.FileKind;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.interfaces.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberQueryUseCase memberQueryUseCase;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    //    private final FcmUseCase fcmUseCase;
    private final TokenUseCase tokenUseCase;
    private final StoragePort storagePort;
    private final MemberSearchPort memberSearchPort;
    private final MemberSearchCachePort memberSearchCachePort;
    private final MemberSearchSuggestionsCachePort memberSearchSuggestionsCachePort;
    private final MemberRecentViewCachePort memberRecentViewCachePort;

    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    @Override
    public MemberSignResponseDto create(MemberSignDto memberSignDto) {
        if (memberRepository.exist(memberSignDto.getEmail())) {
            throw new ConflictException("이미 가입된 회원입니다.");
        }
        String imageFileName = storagePort.uploadFile(memberSignDto.getMemberImageUrl(), FileKind.MEMBER);
        Member member = MemberMapper.toEntity(memberSignDto, passwordEncoder.encode(memberSignDto.getPassword()), imageFileName);
        Member savedMember = memberRepository.save(member);

        //outbox 이벤트 발행
        eventPublisher.publishEvent(MemberEvent.builder()
                .methodType(MethodType.CREATE)
                .memberId(savedMember.getId())
                .memberName(savedMember.getName())
                .memberNameChosung(NameChosungUtil.getChosung(memberSignDto.getName()))
                .memberImageUrl(savedMember.getMemberImageUrl())
                .build()
        );

        return new MemberSignResponseDto(savedMember.getId());
    }

    @Override
    public FindByIdResponseDto findById(String phoneNumber) {
        Member member = memberQueryUseCase.findOrThrowByPhoneNumber(phoneNumber);
        return new FindByIdResponseDto(member.getEmail());
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
        Member member = memberQueryUseCase.findOrThrow(targetId);

        memberRecentViewCachePort.create(memberId, targetId); // 최근 본 회원 캐시에 저장

        return MemberMapper.toGetMemberResponseDto(member);
    }

    @Override
    @Transactional
    public void update(UpdateMemberRequestDto requestDto, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        String imageFileName = storagePort.uploadFile(requestDto.getMemberImageUrl(), FileKind.MEMBER);

        member.setName(requestDto.getName());
        String chosung = NameChosungUtil.getChosung(requestDto.getName());
        member.setMemberImageUrl(imageFileName);

        eventPublisher.publishEvent(MemberEvent.builder()
                .methodType(MethodType.UPDATE)
                .memberId(memberId)
                .memberName(requestDto.getName())
                .memberNameChosung(chosung)
                .memberImageUrl(imageFileName)
                .build()
        );
    }

    @Transactional
    @Override
    public void delete(Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
        tokenUseCase.delete(memberId);
        memberRepository.delete(member);

        eventPublisher.publishEvent(MemberEvent.builder()
                .methodType(MethodType.DELETE)
                .memberId(memberId)
                .build()
        );
    }

//    @Transactional
//    @Override
//    public void createFcmToken(FcmTokenDto fcmTokenDto) {
//        Member member = memberQueryUseCase.findOrThrow(fcmTokenDto.getMemberId());
//        fcmUseCase.createFcmToken(member, fcmTokenDto.getFcmToken());
//    }

    @Override
    public List<MemberSearchResponseDto> searchSuggestions(String keyword, Long memberId) {
        if (keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("키워드를 입력해주세요.");
        }
        /**
         * 사실 matchQuery만 사용할 때는 필요없으나 termQuery 때문에 핸들 거치고 검색요청해야함.
         */
        String key = keywordFilter(keyword);
        List<MemberSearchResponseDto> result = memberSearchSuggestionsCachePort.get(key);

        if (result == null) {
            result = memberSearchPort.searchSuggestions(key);// 캐시 미스면 db에서 조회
            memberSearchSuggestionsCachePort.create(key, result);//해당 자동완성에 캐싱

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
    public List<MemberSearchResponseDto> searchMembers(String keyword, int page, Long memberId) {
        if (keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("키워드를 입력해주세요.");
        }
        String key = keywordFilter(keyword);
        List<MemberSearchResponseDto> reuslt = memberSearchCachePort.get(key, page);
        if (reuslt == null) {
            reuslt = memberSearchPort.search(key, page);
            memberSearchCachePort.create(key, page, reuslt);
        }
        return reuslt;
    }


    private String keywordFilter(String keyword) {
        return keyword.replaceAll("\\s+", "").toLowerCase();
    }
}