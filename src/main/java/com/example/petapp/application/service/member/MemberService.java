package com.example.petapp.application.service.member;

import com.example.petapp.application.common.imagefile.FileImageKind;
import com.example.petapp.application.common.imagefile.FileUploadUtil;
import com.example.petapp.application.in.email.EmailUseCase;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.member.dto.request.*;
import com.example.petapp.application.in.member.dto.response.FindByIdResponseDto;
import com.example.petapp.application.in.member.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.dto.response.MemberSignResponseDto;
import com.example.petapp.application.in.member.mapper.MemberMapper;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.domain.fcm.FcmTokenService;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.RoleRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.member.model.MemberRole;
import com.example.petapp.domain.member.model.Role;
import com.example.petapp.interfaces.exception.ConflictException;
import com.example.petapp.interfaces.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final FcmTokenService fcmTokenService;
    private final RoleRepository roleRepository;
    @Value("${spring.dog.member.image.upload}")
    private String memberUploadDir;

    @Transactional
    @Override
    public MemberSignResponseDto createMember(MemberSignDto memberSignDto) {
        if (memberRepository.exist(memberSignDto.getEmail())) {
            throw new ConflictException("이미 가입된 회원입니다.");
        }
        String imageFileName = FileUploadUtil.fileUpload(memberSignDto.getMemberImageUrl(), memberUploadDir, FileImageKind.MEMBER);
        Member member = MemberMapper.toEntity(memberSignDto, passwordEncoder.encode(memberSignDto.getPassword()), imageFileName);
        Member savedMember = memberRepository.save(member);
        return new MemberSignResponseDto(savedMember.getId());
    }

    @Transactional
    @Override
    public LoginResponseDto login(LoginDto loginDto, HttpServletResponse response) {
        Member member = memberQueryUseCase.findOrThrow(loginDto.getEmail());
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new UnAuthorizedException("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        setRole(member);
        return tokenUseCase.save(member);
    }

    @Override
    public AccessTokenResponseDto verifyCode(String email, String code) {//sendEmail할 때 이메일 유효성 검사 했으므로 안해줘도 됨.
        emailUseCase.verifyCode(email, code);
        return tokenUseCase.createResetPasswordJwt(email);
    }

    @Transactional(readOnly = true)
    @Override
    public FindByIdResponseDto findById(String phoneNumber) {
        Member member = memberQueryUseCase.findOrThrowByPhoneNumber(phoneNumber);
        return new FindByIdResponseDto(member.getEmail());
    }

    @Transactional(readOnly = true)
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
    public void resetPassword(ResetPasswordDto resetPasswordDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        if (member.isSamePassword(passwordEncoder, resetPasswordDto.getNewPassword())) {
            throw new IllegalArgumentException("전 비밀번호와 다르게 설정해야합니다.");
        } else {
            member.updatePassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        }
    }

    @Transactional(readOnly = true)//상세 멤버 프로필 추가랑 어떤거 해야할지 해야됨. 여기에 자기가 쓴 게시물, 산책길 추천, 후기 추가해야할듯.
    @Override
    public GetMemberResponseDto getMember(Long memberId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        return MemberMapper.toGetMemberResponseDto(member);
    }

    @Transactional
    @Override
    public void deleteMember(String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        memberRepository.delete(member);
    }

    @Transactional
    @Override
    public void createFcmToken(FcmTokenDto fcmTokenDto) {
        Member member = memberQueryUseCase.findOrThrow(fcmTokenDto.getMemberId());
        fcmTokenService.createFcmToken(member, fcmTokenDto.getFcmToken());
    }

    private void setRole(Member member) {
        Role role = roleRepository.find("ROLE_USER").get();
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();
        member.addRole(memberRole);
    }
}
