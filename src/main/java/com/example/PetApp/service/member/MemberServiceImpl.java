package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.MemberRole;
import com.example.PetApp.domain.Role;
import com.example.PetApp.dto.member.*;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.exception.UnAuthorizedException;
import com.example.PetApp.mapper.MemberMapper;
import com.example.PetApp.repository.jpa.MemberRepository;
import com.example.PetApp.repository.jpa.RoleRepository;
import com.example.PetApp.service.email.EmailService;
import com.example.PetApp.service.fcm.FcmTokenService;
import com.example.PetApp.service.query.QueryService;
import com.example.PetApp.service.token.TokenService;
import com.example.PetApp.util.imagefile.FileUploadUtil;
import com.example.PetApp.util.imagefile.FileImageKind;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    @Value("${spring.dog.member.image.upload}")
    private String memberUploadDir;

    private final QueryService queryService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final FcmTokenService fcmTokenService;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public MemberSignResponseDto createMember(MemberSignDto memberSignDto) {
        if (memberRepository.existsByEmail(memberSignDto.getEmail())) {
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
        Member member = queryService.findByMember(loginDto.getEmail());
        if (!passwordEncoder.matches(loginDto.getPassword(),member.getPassword())) {
            throw new UnAuthorizedException("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }
        setRole(member);
        return tokenService.save(member, response);
    }

    @Override
    public AccessTokenResponseDto verifyCode(String email, String code) {//sendEmail할 때 이메일 유효성 검사 했으므로 안해줘도 됨.
        emailService.verifyCode(email, code);
        return tokenService.createResetPasswordJwt(email);
    }

    @Transactional(readOnly = true)
    @Override
    public FindByIdResponseDto findById(String phoneNumber) {
        Member member = queryService.findByMemberToPhoneNumber(phoneNumber);
        return new FindByIdResponseDto(member.getEmail());
    }

    @Transactional(readOnly = true)
    @Override
    public void sendEmail(SendEmailDto sendEmailDto) {
        Member member = queryService.findByMember(sendEmailDto.getEmail());
        emailService.sendMail(member.getEmail());
    }


    @Override
    public void logout(String accessToken) {
        tokenService.deleteRefreshToken(accessToken);
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return queryService.findByMember(email);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, String email) {
        Member member = queryService.findByMember(email);
        if (passwordEncoder.matches(resetPasswordDto.getNewPassword(),member.getPassword())) {
            throw new IllegalArgumentException("전 비밀번호와 다르게 설정해야합니다.");
        } else {
            member.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        }
    }

    @Transactional(readOnly = true)//상세 멤버 프로필 추가랑 어떤거 해야할지 해야됨. 여기에 자기가 쓴 게시물, 산책길 추천, 후기 추가해야할듯.
    @Override
    public GetMemberResponseDto getMember(Long memberId, String email) {
        Member member = queryService.findByMember(email);
        return MemberMapper.toGetMemberResponseDto(member);
    }

    @Transactional
    @Override
    public void deleteMember(String email) {
        Member member = queryService.findByMember(email);
        memberRepository.delete(member);
    }

    @Transactional
    @Override
    public void createFcmToken(FcmTokenDto fcmTokenDto) {
        Member member = queryService.findByMember(fcmTokenDto.getMemberId());
        fcmTokenService.createFcmToken(member, fcmTokenDto.getFcmToken());
    }

    private void setRole(Member member) {
        Role role = roleRepository.findByName("ROLE_USER").get();
        MemberRole memberRole=MemberRole.builder()
                .member(member)
                .role(role)
                .build();
        member.addRole(memberRole);
    }
}
