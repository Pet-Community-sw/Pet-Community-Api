package com.example.PetApp.domain.token;

import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.common.exception.NotFoundException;
import com.example.PetApp.common.exception.UnAuthorizedException;
import com.example.PetApp.common.jwt.util.JwtTokenizer;
import com.example.PetApp.common.util.RedisUtil;
import com.example.PetApp.domain.member.RoleRepository;
import com.example.PetApp.domain.member.mapper.MemberMapper;
import com.example.PetApp.domain.member.model.dto.request.AccessTokenResponseDto;
import com.example.PetApp.domain.member.model.dto.response.TokenResponseDto;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.member.model.entity.Role;
import com.example.PetApp.domain.token.model.entity.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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
    private final RedisUtil redisUtil;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public TokenResponseDto save(Member member, HttpServletResponse response) {
        Optional<RefreshToken> byMember = refreshRepository.findByMember(member);
        List<String> roles = member.getMemberRoles().stream().map(memberRole -> memberRole.getRole().getName()).collect(Collectors.toList());
        /*
        * JWT는 직렬화된 토큰 문자열이며,
            그 안에 Role이라는 도메인 객체 전체를 넣는 건 부적절하기 때문입니다
        *
        *
        * */

        String accessToken = jwtTokenizer.createAccessToken(member.getId(), null, member.getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getId(), member.getEmail(), roles);

        saveAndSendRefreshToken(member, response, byMember, refreshToken);
        log.info("로그인 요청 성공");
        return MemberMapper.toLoginResponseDto(refreshToken, accessToken);
    }

    private void saveAndSendRefreshToken(Member member, HttpServletResponse response, Optional<RefreshToken> byMember, String refreshToken) {
        if (byMember.isEmpty()) {
            RefreshToken refreshToken1 = RefreshToken.builder()
                    .member(member)
                    .refreshToken(refreshToken)
                    .build();
            refreshRepository.save(refreshToken1);
        } else {
            byMember.get().setRefreshToken(refreshToken);
        }
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // 자바스크립트 접근 불가
        cookie.setSecure(true);   // HTTPS에서만 전송
        cookie.setPath("/");      // 모든 경로에서 유효
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(cookie);
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

        Claims claims = getClaimsFromToken(refreshToken, TokenType.REFRESH);
        Long memberId = Long.valueOf((Integer) claims.get("memberId"));
        RefreshToken savedRefreshToken = refreshRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("refreshToken이 없음. 다시 로그인."));

        if (!savedRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new ForbiddenException("RefreshToken이 유효하지 않습니다.");
        }

        return createNewToken(claims, memberId, savedRefreshToken);
    }

    @NotNull
    private TokenResponseDto createNewToken(Claims claims, Long memberId, RefreshToken refreshToken) {
        if (jwtTokenizer.isTokenExpired(TokenType.REFRESH, refreshToken.getRefreshToken())) {
            throw new UnAuthorizedException("로그인 다시 해야됨.");
        } else {
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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
    public String newAccessTokenByProfile(String accessToken, String refreshToken, Member member, Long profileId) {
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
        redisUtil.createData(accessToken, "blacklist", 30 * 60L);
    }

    private Claims getClaimsFromToken(String token, TokenType tokenType) {
        if (tokenType == TokenType.ACCESS) {
            try {
                return jwtTokenizer.parseAccessToken(token);
            } catch (ExpiredJwtException e) {
                return e.getClaims();
            }
        } else if (tokenType == TokenType.REFRESH) {
            try {
                return jwtTokenizer.parseRefreshToken(token);
            } catch (ExpiredJwtException e) {
                return e.getClaims();
            }
        } else
            throw new IllegalArgumentException("잘못된 tokenType");
    }

    @NotNull
    private static List<String> getRoles(Member member) {
        return member
                .getMemberRoles()
                .stream()
                .map(memberRole -> memberRole.getRole().getName())
                .collect(Collectors.toList());
    }

}
