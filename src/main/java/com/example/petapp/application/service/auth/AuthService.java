package com.example.petapp.application.service.auth;

import com.example.petapp.application.in.auth.AuthUseCase;
import com.example.petapp.application.in.email.EmailUseCase;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.member.object.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.in.member.object.dto.request.AuthCodeDto;
import com.example.petapp.application.in.member.object.dto.request.LoginDto;
import com.example.petapp.application.in.member.object.dto.request.SendEmailDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.application.in.role.RoleQueryUseCase;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.member.model.MemberRole;
import com.example.petapp.domain.role.Role;
import com.example.petapp.interfaces.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final MemberQueryUseCase memberQueryUseCase;
    private final TokenUseCase tokenUseCase;
    private final EmailUseCase emailUseCase;
    private final RoleQueryUseCase roleQueryUseCase;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public LoginResponseDto login(LoginDto loginDto) {
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
    public void sendEmail(SendEmailDto sendEmailDto) {
        Member member = memberQueryUseCase.findOrThrow(sendEmailDto.getEmail());
        emailUseCase.send(member.getId(), member.getEmail());
    }

    @Override
    public void logout(String accessToken) {
        tokenUseCase.delete(accessToken);
    }

    private void setRole(Member member, Role role) {
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();
        member.addRole(memberRole);
    }
}
