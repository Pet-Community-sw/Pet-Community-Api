package com.example.petapp.application.in.fcm;

import com.example.petapp.domain.member.model.Member;

public interface FcmUseCase {

    void createFcmToken(Member member, String token);
}
