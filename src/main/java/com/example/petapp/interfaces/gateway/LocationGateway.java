package com.example.petapp.interfaces.gateway;

import com.example.petapp.application.in.location.LocationUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor//서버에서 클라이언트에게만 보냄으로 sse로 해야됨.
public class LocationGateway {
    private final LocationUseCase locationUseCase;

    @MessageMapping("/location")
    public void sendLocation(@Payload LocationMessage locationMessage, Principal principal) {
        locationUseCase.sendLocation(locationMessage, principal.getName());
    }
}
