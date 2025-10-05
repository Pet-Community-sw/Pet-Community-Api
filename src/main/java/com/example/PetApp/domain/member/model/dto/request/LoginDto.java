package com.example.PetApp.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

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
