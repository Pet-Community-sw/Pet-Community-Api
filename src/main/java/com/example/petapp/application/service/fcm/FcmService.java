package com.example.petapp.application.service.fcm;

import com.example.petapp.application.in.fcm.FcmUseCase;
import com.example.petapp.domain.fcm.FcmRepository;
import com.example.petapp.domain.fcm.model.FcmToken;
import com.example.petapp.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService implements FcmUseCase {

    private final FcmRepository fcmRepository;

    @Override
    public void createFcmToken(Member member, String token) {
        FcmToken fcmToken = FcmToken.builder()
                .member(member)
                .fcmToken(token)
                .build();
        fcmRepository.save(fcmToken);
    }

    //업데이트 로직.

}
