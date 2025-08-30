package com.example.PetApp.domain.fcm;

import com.example.PetApp.domain.member.model.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface FcmTokenService {

    void createFcmToken(Member member, String token);
}
