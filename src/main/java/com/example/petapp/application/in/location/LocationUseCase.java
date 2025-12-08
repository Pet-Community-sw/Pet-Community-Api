package com.example.petapp.application.in.location;

import com.example.petapp.application.in.location.dto.request.LocationMessage;

public interface LocationUseCase {
    void sendLocation(LocationMessage locationMessage, String memberId);
}
