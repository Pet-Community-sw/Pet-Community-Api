package com.example.petapp.domain.token;

import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.common.exception.UnAuthorizedException;
import com.example.petapp.common.jwt.util.JwtTokenizer;
import com.example.petapp.domain.member.RoleRepository;
import com.example.petapp.domain.member.mapper.MemberMapper;
import com.example.petapp.domain.member.model.dto.request.AccessTokenResponseDto;
import com.example.petapp.domain.member.model.dto.response.LoginResponseDto;
import com.example.petapp.domain.member.model.dto.response.TokenResponseDto;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.member.model.entity.Role;
import com.example.petapp.domain.token.model.entity.RefreshToken;
import com.example.petapp.port.InMemoryService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {//리펙토링 필요.

    private final RefreshRepository refreshRepository;
    private final JwtTokenizer jwtTokenizer;
    private final InMemoryService inMemoryService;
    private final RoleRepository roleRepository;

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
    public LoginResponseDto save(Member member) {
        List<String> roles = member.getMemberRoles().stream().map(memberRole -> memberRole.getRole().getName()).collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.getId(), null, member.getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getId(), member.getEmail(), roles);

        log.info("로그인 요청 성공");
        return MemberMapper.toLoginResponseDto(member, refreshToken, accessToken);
    }

    @Transactional
    @Override
    public TokenResponseDto reissueToken(String header) {
        log.info("토큰 재요청.");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnAuthorizedException("헤더가 null이거나 Bearer로 시작하지않음");
        }
        String[] str = header.split(" ");
        String refreshToken = str[1];

        Claims claims = getClaimsFromToken(refreshToken);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        RefreshToken savedRefreshToken = refreshRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("refreshToken이 없음. 다시 로그인."));

        savedRefreshToken.isEqual(refreshToken);

        return createNewToken(claims, memberId, savedRefreshToken);
    }

    @NotNull
    private TokenResponseDto createNewToken(Claims claims, Long memberId, RefreshToken refreshToken) {
        if (jwtTokenizer.isTokenExpired(TokenType.REFRESH, refreshToken.getRefreshToken())) {
            throw new UnAuthorizedException("로그인 다시 해야됨.");
        } else {
            List<String> roles = (List<String>) claims.get("roles");
            String email = claims.getSubject();
            Optional<Object> profileId = Optional.ofNullable(claims.get("profileId"));//refresh에서 profileId를 꺼내는것이 보안상 좋을 듯한데
            String newAccessToken;//getProfileId를 했을 때 null이면 일반 토큰 있으면 profile토큰
            if (profileId.isEmpty()) {
                newAccessToken = jwtTokenizer.createAccessToken(memberId, null, email, roles);
            } else
                newAccessToken = jwtTokenizer.createAccessToken(memberId, Long.valueOf(profileId.toString()), email, roles);//profile이있으면 붙혀서 반환.
            String newRefreshToken = jwtTokenizer.createRefreshToken(memberId, email, roles);
            refreshToken.setRefreshToken(newRefreshToken);
            return new TokenResponseDto(newAccessToken, newRefreshToken);
        }
    }

    @Override
    public AccessTokenResponseDto createResetPasswordJwt(String email) {
        List<Role> roles = new ArrayList<>();
        Role role = roleRepository.findByName("ROLE_USER").get();
        roles.add(role);
        String resetPasswordToken = jwtTokenizer.createResetPasswordToken(email, roles.stream().map(Role::getName).collect(Collectors.toList()));
        return new AccessTokenResponseDto(resetPasswordToken);
    }

    @Override
    public String newAccessTokenByProfile(String accessToken, Member member, Long profileId) {
        blacklistAccessToken(accessToken);
        List<String> roles = getRoles(member);
        return jwtTokenizer.createAccessToken(member.getId(), profileId, member.getEmail(), roles);
    }

    @Transactional
    @Override
    public void deleteRefreshToken(String accessToken) {
        log.info("deleteRefreshToken 요청");
        String[] arr = accessToken.split(" ");
        Claims claims = jwtTokenizer.parseAccessToken(arr[1]);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        refreshRepository.deleteByMemberId(memberId);
        blacklistAccessToken(accessToken);
    }

    private void blacklistAccessToken(String accessToken) {
        inMemoryService.createStringDataWithDuration(accessToken, "blacklist", 30 * 60L);
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return jwtTokenizer.parseRefreshToken(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
