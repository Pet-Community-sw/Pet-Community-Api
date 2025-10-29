package com.example.petapp.domain.token;

import com.example.petapp.domain.member.model.dto.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Token")
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    @Operation(
            summary = "토큰 재발급"
    )
    @ApiResponse(description = "상단 Authorization에 refreshToken넣고 요청.")
    @PostMapping
    public TokenResponseDto reissueToken(@Parameter(hidden = true) @RequestHeader("Authorization") String refreshToken, @RequestBody() String accessToken) {
        return tokenService.reissueToken(refreshToken, accessToken);
    }
}
