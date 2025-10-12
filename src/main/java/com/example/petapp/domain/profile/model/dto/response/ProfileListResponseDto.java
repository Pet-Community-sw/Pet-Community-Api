package com.example.petapp.domain.profile.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileListResponseDto {

    private String petImageUrl;

    private String petName;

    private Long profileId;

    private boolean hasBirthday;

}
