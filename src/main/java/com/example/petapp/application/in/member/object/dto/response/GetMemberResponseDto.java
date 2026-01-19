package com.example.petapp.application.in.member.object.dto.response;

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
