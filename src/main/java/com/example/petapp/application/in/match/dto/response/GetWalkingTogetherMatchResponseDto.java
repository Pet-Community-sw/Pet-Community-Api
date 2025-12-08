package com.example.petapp.application.in.match.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetWalkingTogetherMatchResponseDto {
    private Long walkingTogetherPostId;

    private LocalDateTime scheduledTime;

    private Long profileId;

    private String petName;

    private String petImageUrl;

    private int currentCount;

    private int limitCount;

    private String createdAt;

    private boolean isOwner;

    private boolean filtering;

}
