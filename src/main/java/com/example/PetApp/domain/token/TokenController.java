package com.example.PetApp.domain.token;

import com.example.PetApp.domain.member.model.dto.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    @ApiResponse(description = "상단 Authorization에 refreshToken넣고 요청.")
    @PostMapping
    public TokenResponseDto reissueToken(@Parameter(hidden = true) @RequestHeader("Authorization") String refreshToken) {
        return tokenService.reissueToken(refreshToken);
    }
}
