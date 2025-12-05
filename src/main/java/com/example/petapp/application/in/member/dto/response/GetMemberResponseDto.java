package com.example.petapp.application.in.member.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMemberResponseDto {

    private String memberName;

    private String memberImageUrl;
}
