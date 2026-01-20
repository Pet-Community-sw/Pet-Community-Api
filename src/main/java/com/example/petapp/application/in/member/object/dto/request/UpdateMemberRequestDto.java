package com.example.petapp.application.in.member.object.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMemberRequestDto {

    @NotBlank(message = "이름 입력은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣\\\\s]{2,7}",
            message = "이름은 영문자, 한글, 공백포함 2글자부터 7글자까지 가능합니다.")
    @Schema(example = "최선재")
    private String name;
}
