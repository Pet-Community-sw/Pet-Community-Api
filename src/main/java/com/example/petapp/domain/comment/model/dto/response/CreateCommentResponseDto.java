package com.example.petapp.domain.comment.model.dto.response;

import com.example.petapp.common.base.util.notification.NotificationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateCommentResponseDto {

    private Long commentId;

    @JsonIgnore
    private NotificationDto notificationDto;
}
