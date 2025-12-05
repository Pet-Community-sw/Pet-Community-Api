package com.example.petapp.domain.fcm;

import com.example.petapp.domain.member.model.Member;
import org.springframework.stereotype.Service;

@Service
public interface FcmTokenService {

    void createFcmToken(Member member, String token);
}
