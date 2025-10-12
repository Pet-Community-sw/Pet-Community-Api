package com.example.petapp.domain.fcm;

import com.example.petapp.domain.fcm.model.entity.FcmToken;
import com.example.petapp.domain.member.model.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    @Override
    public void createFcmToken(Member member, String token) {
        FcmToken fcmToken=FcmToken.builder()
                .member(member)
                .fcmToken(token)
                .build();
        fcmTokenRepository.save(fcmToken);
    }

    //업데이트 로직.

}
