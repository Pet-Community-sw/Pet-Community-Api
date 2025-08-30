package com.example.PetApp.domain.token;

import com.example.PetApp.domain.member.model.dto.request.AccessTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public AccessTokenResponseDto reissueAccessToken(@RequestHeader("Authorization") String accessToken, @CookieValue("refreshToken") String refreshToken) {
        return tokenService.reissueAccessToken(accessToken, refreshToken);
    }
}
