package com.example.PetApp.domain.walkrecord.model.dto.response;

import com.example.PetApp.common.base.util.notification.NotificationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateWalkRecordResponseDto {
    private Long walkRecordId;

    @JsonIgnore
    private NotificationDto notificationDto;

}
