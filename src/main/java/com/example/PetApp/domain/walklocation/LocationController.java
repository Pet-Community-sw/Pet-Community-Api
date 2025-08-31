package com.example.PetApp.domain.walklocation;

import com.example.PetApp.domain.walklocation.model.dto.request.LocationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor//서버에서 클라이언트에게만 보냄으로 sse로 해야됨.
public class LocationController {
    private final LocationService locationService;

    @MessageMapping("/location/send")
    public void sendLocation(LocationMessage locationMessage, Principal principal) {
        String memberId = principal.getName();
        locationService.sendLocation(locationMessage, memberId);
    }
}
