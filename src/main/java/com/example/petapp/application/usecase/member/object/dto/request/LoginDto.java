package com.example.petapp.application.usecase.member.object.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Schema(
            example = "chltjswo@naver.com"
    )
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(example = "sunjaeJang12!")
    private String password;

}
