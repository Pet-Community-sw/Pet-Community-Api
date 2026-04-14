package com.example.petapp.application.service.auth;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberQueryUseCase memberQueryUseCase;

    @Mock
    private TokenUseCase tokenUseCase;

    @Mock
    private EmailUseCase emailUseCase;

    @Mock
    private RoleQueryUseCase roleQueryUseCase;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void 로그인하면_토큰을_반환하고_사용자_역할을_추가한다() {
        LoginDto loginDto = LoginDto.builder()
                .email("user@test.com")
                .password("plain-password")
                .build();
        Member member = 회원을_생성한다(1L, "tester", "user@test.com", "encoded-password");
        Role role = Role.builder()
                .name("ROLE_USER")
                .build();
        LoginResponseDto expected = LoginResponseDto.builder()
                .memberId(1L)
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(memberQueryUseCase.findOrThrow(loginDto.getEmail())).thenReturn(member);
        when(passwordEncoder.matches(loginDto.getPassword(), member.getPassword())).thenReturn(true);
        when(roleQueryUseCase.findUserRole()).thenReturn(role);
        when(tokenUseCase.save(member, role)).thenReturn(expected);

        LoginResponseDto response = authService.login(loginDto);

        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(member.getMemberRoles()).hasSize(1);
        MemberRole memberRole = member.getMemberRoles().get(0);
        assertThat(memberRole.getMember()).isSameAs(member);
        assertThat(memberRole.getRole()).isSameAs(role);
    }

    @Test
    void 비밀번호가_일치하지_않으면_로그인에서_예외가_발생한다() {
        LoginDto loginDto = LoginDto.builder()
                .email("user@test.com")
                .password("wrong-password")
                .build();
        Member member = 회원을_생성한다(1L, "tester", "user@test.com", "encoded-password");

        when(memberQueryUseCase.findOrThrow(loginDto.getEmail())).thenReturn(member);
        when(passwordEncoder.matches(loginDto.getPassword(), member.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(UnAuthorizedException.class)
                .hasMessage("이메일 혹은 비밀번호가 일치하지 않습니다.");

        verify(tokenUseCase, never()).save(any(Member.class), any(Role.class));
    }

    @Test
    void 인증코드_검증후_비밀번호재설정_토큰을_발급한다() {
        AuthCodeDto authCodeDto = AuthCodeDto.builder()
                .email("user@test.com")
                .code("123456")
                .build();
        Member member = 회원을_생성한다(1L, "tester", "user@test.com", "encoded-password");
        AccessTokenResponseDto expected = AccessTokenResponseDto.builder()
                .newAccessToken("temporary-token")
                .build();

        when(memberQueryUseCase.findOrThrow(authCodeDto.getEmail())).thenReturn(member);
        when(tokenUseCase.createResetPasswordJwt(member)).thenReturn(expected);

        AccessTokenResponseDto response = authService.verifyCode(authCodeDto);

        assertThat(response.getNewAccessToken()).isEqualTo("temporary-token");
        verify(emailUseCase).verifyCode("user@test.com", "123456");
        verify(tokenUseCase).createResetPasswordJwt(member);
    }

    @Test
    void 이메일전송을_요청하면_회원_이메일로_발송한다() {
        SendEmailDto sendEmailDto = SendEmailDto.builder()
                .email("user@test.com")
                .build();
        Member member = 회원을_생성한다(1L, "tester", "user@test.com", "encoded-password");

        when(memberQueryUseCase.findOrThrow(sendEmailDto.getEmail())).thenReturn(member);

        authService.sendEmail(sendEmailDto);

        verify(emailUseCase).send(1L, "user@test.com");
    }

    @Test
    void 로그아웃하면_토큰_삭제를_위임한다() {
        authService.logout("Bearer access-token");

        verify(tokenUseCase).delete("Bearer access-token");
    }

    private Member 회원을_생성한다(Long id, String name, String email, String password) {
        return Member.builder()
                .id(id)
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber("01012345678")
                .memberImageUrl("image.png")
                .build();
    }
}
