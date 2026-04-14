package com.example.petapp.application.service.token;

import com.example.petapp.application.in.member.object.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.object.dto.response.TokenResponseDto;
import com.example.petapp.application.in.role.RoleQueryUseCase;
import com.example.petapp.application.in.token.MemberInfo;
import com.example.petapp.application.in.token.TokenQueryUseCase;
import com.example.petapp.application.in.token.dto.ReissueTokenRequestDto;
import com.example.petapp.application.out.TokenPort;
import com.example.petapp.application.out.cache.TokenCachePort;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.member.model.MemberRole;
import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.token.TokenRepository;
import com.example.petapp.domain.token.model.Token;
import com.example.petapp.domain.token.model.TokenType;
import com.example.petapp.interfaces.exception.UnAuthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenCachePort tokenCachePort;

    @Mock
    private RoleQueryUseCase roleQueryUseCase;

    @Mock
    private TokenQueryUseCase tokenQueryUseCase;

    @Mock
    private TokenPort tokenPort;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void 신규로그인시_액세스와_리프레시_토큰을_생성한다() {
        Member member = 회원을_생성한다(1L, "tester", "user@test.com");
        Role role = Role.builder()
                .name("ROLE_USER")
                .build();

        when(tokenPort.create(TokenType.ACCESS, 1L, null, "tester", List.of("ROLE_USER"))).thenReturn("access-token");
        when(tokenPort.create(TokenType.REFRESH, 1L, null, "tester", List.of("ROLE_USER"))).thenReturn("refresh-token");
        when(tokenQueryUseCase.find(1L)).thenReturn(Optional.empty());

        LoginResponseDto response = tokenService.save(member, role);

        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getMember()).isSameAs(member);
        assertThat(tokenCaptor.getValue().getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void 기존_리프레시토큰이_있으면_갱신한다() {
        Member member = 회원을_생성한다(1L, "tester", "user@test.com");
        Role role = Role.builder()
                .name("ROLE_USER")
                .build();
        Token savedToken = Token.builder()
                .member(member)
                .refreshToken("old-refresh-token")
                .build();

        when(tokenPort.create(TokenType.ACCESS, 1L, null, "tester", List.of("ROLE_USER"))).thenReturn("access-token");
        when(tokenPort.create(TokenType.REFRESH, 1L, null, "tester", List.of("ROLE_USER"))).thenReturn("new-refresh-token");
        when(tokenQueryUseCase.find(1L)).thenReturn(Optional.of(savedToken));

        LoginResponseDto response = tokenService.save(member, role);

        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(savedToken.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(tokenRepository, never()).save(org.mockito.ArgumentMatchers.any(Token.class));
    }

    @Test
    void 토큰재발급시_리프레시토큰을_교체하고_기존_액세스토큰을_블랙리스트에_등록한다() {
        Member member = 회원을_생성한다(1L, "tester", "user@test.com");
        Token storedToken = Token.builder()
                .member(member)
                .refreshToken("stored-refresh-token")
                .build();
        ReissueTokenRequestDto requestDto = new ReissueTokenRequestDto("stored-refresh-token");
        MemberInfo accessInfo = MemberInfo.builder()
                .memberId(1L)
                .build();
        MemberInfo refreshInfo = MemberInfo.builder()
                .memberId(1L)
                .profileId(2L)
                .name("tester")
                .roles(List.of("ROLE_USER"))
                .build();

        when(tokenPort.getInfo(TokenType.ACCESS, "old-access-token")).thenReturn(accessInfo);
        when(tokenQueryUseCase.findOrThrow(1L)).thenReturn(storedToken);
        when(tokenPort.getInfo(TokenType.REFRESH, "stored-refresh-token")).thenReturn(refreshInfo);
        when(tokenPort.create(TokenType.ACCESS, 1L, 2L, "tester", List.of("ROLE_USER"))).thenReturn("new-access-token");
        when(tokenPort.create(TokenType.REFRESH, 1L, null, "tester", List.of("ROLE_USER"))).thenReturn("new-refresh-token");

        TokenResponseDto response = tokenService.reissueToken("Bearer old-access-token", requestDto);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(storedToken.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(tokenCachePort).create("blacklist", "old-access-token", 30 * 60L);
    }

    @Test
    void 인가_헤더가_유효하지_않으면_토큰재발급에서_예외가_발생한다() {
        ReissueTokenRequestDto requestDto = new ReissueTokenRequestDto("refresh-token");

        assertThatThrownBy(() -> tokenService.reissueToken("invalid-header", requestDto))
                .isInstanceOf(UnAuthorizedException.class)
                .hasMessage("헤더가 null이거나 Bearer로 시작하지않음");
    }

    @Test
    void 비밀번호재설정토큰은_TEMPORARY_역할을_사용한다() {
        Member member = 회원을_생성한다(1L, "tester", "user@test.com");
        Role temporaryRole = Role.builder()
                .name("ROLE_TEMPORARY")
                .build();

        when(roleQueryUseCase.findTemporaryRole()).thenReturn(temporaryRole);
        when(tokenPort.create(TokenType.EMAIL_ACCESS, 1L, null, null, List.of("ROLE_TEMPORARY"))).thenReturn("temporary-token");

        AccessTokenResponseDto response = tokenService.createResetPasswordJwt(member);

        assertThat(response.getNewAccessToken()).isEqualTo("temporary-token");
    }

    @Test
    void 프로필기반_새액세스토큰발급시_기존토큰을_블랙리스트처리하고_회원역할을_사용한다() {
        Member member = 회원을_생성한다(1L, "tester", "user@test.com");
        Role role = Role.builder()
                .name("ROLE_USER")
                .build();
        member.addRole(MemberRole.builder()
                .member(member)
                .role(role)
                .build());

        when(tokenPort.create(TokenType.ACCESS, 1L, 3L, "user@test.com", List.of("ROLE_USER"))).thenReturn("profile-access-token");

        String newAccessToken = tokenService.newAccessTokenByProfile("old-access-token", member, 3L);

        assertThat(newAccessToken).isEqualTo("profile-access-token");
        verify(tokenCachePort).create("blacklist", "old-access-token", 30 * 60L);
    }

    @Test
    void 삭제시_저장된_리프레시토큰을_제거하고_액세스토큰을_블랙리스트에_등록한다() {
        MemberInfo accessInfo = MemberInfo.builder()
                .memberId(1L)
                .build();

        when(tokenPort.getInfo(TokenType.ACCESS, "access-token")).thenReturn(accessInfo);

        tokenService.delete("Bearer access-token");

        verify(tokenRepository).delete(1L);
        verify(tokenCachePort).create("blacklist", "access-token", 30 * 60L);
    }

    private Member 회원을_생성한다(Long id, String name, String email) {
        return Member.builder()
                .id(id)
                .name(name)
                .email(email)
                .password("encoded-password")
                .phoneNumber("01012345678")
                .memberImageUrl("image.png")
                .build();
    }
}
