package com.example.petapp.common.base.util.notification;


import com.example.petapp.domain.member.model.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private Member ownerMember;

    private Member member;
}
