package com.example.petapp.domain.member.model.dto.response;

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
