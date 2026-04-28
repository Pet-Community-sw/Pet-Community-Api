package com.example.petapp.application.service.token;

import com.example.petapp.application.in.member.mapper.MemberMapper;
import com.example.petapp.application.in.member.object.dto.request.AccessTokenResponseDto;
import com.example.petapp.application.in.member.object.dto.response.LoginResponseDto;
import com.example.petapp.application.in.member.object.dto.response.TokenResponseDto;
import com.example.petapp.application.in.role.RoleUseCase;
import com.example.petapp.application.in.token.MemberInfo;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.application.in.token.dto.ReissueTokenRequestDto;
import com.example.petapp.application.out.TokenPort;
import com.example.petapp.application.out.cache.TokenCachePort;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.token.TokenRepository;
import com.example.petapp.domain.token.model.Token;
import com.example.petapp.domain.token.model.TokenType;
import com.example.petapp.interfaces.exception.NotFoundException;
import com.example.petapp.interfaces.exception.UnAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService implements TokenUseCase {//리펙토링 필요.

    private final TokenRepository tokenRepository;
    private final TokenCachePort tokenCachePort;
    private final RoleUseCase roleUseCase;
    private final TokenPort tokenPort;

    @NotNull
    private static List<String> getRoles(Member member) {
        return member
                .getMemberRoles()
                .stream()
                .map(memberRole -> memberRole.getRole().getName())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public LoginResponseDto save(Member member, Role role) {
        List<String> roles = List.of(role.getName());

        String accessToken = tokenPort.create(TokenType.ACCESS, member.getId(), null, member.getName(), roles);
        String refreshToken = tokenPort.create(TokenType.REFRESH, member.getId(), null, member.getName(), roles);

        find(member.getId()).ifPresentOrElse(
                token -> token.updateRefreshToken(refreshToken),
                () -> tokenRepository.save(new Token(member, refreshToken))
        );

        log.info("로그인 요청 성공");
        return MemberMapper.toLoginResponseDto(member, refreshToken, accessToken);
    }

    @Transactional
    @Override
    public TokenResponseDto reissueToken(String header, ReissueTokenRequestDto reissueTokenRequestDto) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnAuthorizedException("헤더가 null이거나 Bearer로 시작하지않음");
        }
        String[] str = header.split(" ");
        String accessToken = str[1];

        MemberInfo info = tokenPort.getInfo(TokenType.ACCESS, accessToken);
        Token refreshToken = findOrThrow(info.getMemberId());
        refreshToken.isEqual(reissueTokenRequestDto.getRefreshToken());
        blacklistAccessToken(accessToken);
        return createNewToken(refreshToken);
    }

    /**
     * 임시 권한을 토큰에 담아 비밀번호 수정 api만 접근할 수 있는 토큰 생성
     */
    @Override
    public AccessTokenResponseDto createResetPasswordJwt(Member member) {
        List<String> roles = List.of(roleUseCase.findTemporaryRole().getName());

        String resetPasswordToken = tokenPort.create(TokenType.EMAIL_ACCESS, member.getId(), null, null, roles);
        return new AccessTokenResponseDto(resetPasswordToken);
    }

    @Override
    public String newAccessTokenByProfile(String accessToken, Member member, Long profileId) {
        blacklistAccessToken(accessToken);
        List<String> roles = getRoles(member);
        return tokenPort.create(TokenType.ACCESS, member.getId(), profileId, member.getEmail(), roles);
    }

    @Transactional
    @Override
    public void delete(String authorization) {
        String[] arr = authorization.split(" ");
        String accessToken = arr[1];
        MemberInfo info = tokenPort.getInfo(TokenType.ACCESS, accessToken);
        tokenRepository.delete(info.getMemberId());
        blacklistAccessToken(accessToken);
    }

    @Override
    public void delete(Long memberId) {
        tokenRepository.delete(memberId);
    }

    private TokenResponseDto createNewToken(Token token) {
        MemberInfo info = tokenPort.getInfo(TokenType.REFRESH, token.getRefreshToken());
        List<String> roles = info.getRoles();
        String name = info.getName();
        Long profileId = info.getProfileId();
        Long memberId = info.getMemberId();
        String newAccessToken = profileId == null ?
                tokenPort.create(TokenType.ACCESS, memberId, null, name, roles)  //getProfileId를 했을 때 null이면 일반 토큰 있으면 profile토큰
                : tokenPort.create(TokenType.ACCESS, memberId, Long.valueOf(profileId.toString()), name, roles);//profile이있으면 붙혀서 반환.
        String newRefreshToken = tokenPort.create(TokenType.REFRESH, memberId, null, name, roles);
        token.setRefreshToken(newRefreshToken);

        return new TokenResponseDto(newAccessToken, newRefreshToken);

    }

    private void blacklistAccessToken(String accessToken) {
        tokenCachePort.create("blacklist", accessToken, 30 * 60L);
    }

    @Transactional(readOnly = true)
    @Override
    public Token findOrThrow(Long id) {
        return tokenRepository.find(id).orElseThrow(() -> new NotFoundException("refreshToken이 없음. 다시 로그인."));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Token> find(Long id) {
        return tokenRepository.find(id);
    }
}
