package com.example.petapp.interfaces;

import com.example.petapp.application.in.member.object.dto.response.TokenResponseDto;
import com.example.petapp.application.in.token.TokenUseCase;
import com.example.petapp.application.in.token.dto.ReissueTokenRequestDto;
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

    private final TokenUseCase tokenUseCase;

    @Operation(
            summary = "토큰 재발급"
    )
    @ApiResponse(description = "상단 Authorization에 accessToken넣고 요청.")
    @PostMapping
    public TokenResponseDto reissueToken(@Parameter(hidden = true) @RequestHeader("Authorization") String accessToken, @RequestBody() ReissueTokenRequestDto reissueTokenRequestDto) {
        return tokenUseCase.reissueToken(accessToken, reissueTokenRequestDto);
    }
}
