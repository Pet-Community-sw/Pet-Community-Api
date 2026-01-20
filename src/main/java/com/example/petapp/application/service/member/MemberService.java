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
import com.example.petapp.application.in.member.object.dto.response.FindByIdResponseDto;
import com.example.petapp.application.in.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSignResponseDto;
import com.example.petapp.application.in.role.RoleQueryUseCase;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.application.out.StoragePort;
import com.example.petapp.domain.file.FileKind;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.member.model.MemberRole;
import com.example.petapp.domain.role.Role;
import com.example.petapp.interfaces.exception.ConflictException;
import com.example.petapp.interfaces.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;


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

    //상세 멤버 프로필 추가랑 어떤거 해야할지 해야됨. 여기에 자기가 쓴 게시물, 산책길 추천, 후기 추가해야할듯.
    @Override
    public GetMemberResponseDto get(Long targetId, Long memberId) {
        Member member = memberQueryUseCase.findOrThrow(memberId);
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

    private void setRole(Member member, Role role) {
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();
        member.addRole(memberRole);
    }
}
