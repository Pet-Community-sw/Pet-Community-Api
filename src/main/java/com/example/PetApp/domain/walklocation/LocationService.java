package com.example.PetApp.domain.walklocation;

import com.example.PetApp.domain.walklocation.model.dto.request.LocationMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationService {
    void sendLocation(LocationMessage locationMessage, String memberId);
}
