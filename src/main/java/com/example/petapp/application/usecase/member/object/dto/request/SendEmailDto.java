package com.example.petapp.application.usecase.member.object.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendEmailDto {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}
