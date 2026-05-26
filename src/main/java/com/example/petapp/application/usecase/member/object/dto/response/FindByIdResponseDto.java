package com.example.petapp.application.usecase.member.object.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindByIdResponseDto {
    private String email;
}
