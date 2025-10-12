package com.example.petapp.domain.post.delegate.model.dto.response;

import com.example.petapp.common.base.util.notification.NotificationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyToDelegateWalkPostResponseDto {

    private Long memberId;

    @JsonIgnore
    private NotificationDto notificationDto;


}
