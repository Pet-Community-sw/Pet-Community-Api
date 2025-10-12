package com.example.petapp.domain.walklocation;

import com.example.petapp.domain.walklocation.model.dto.request.LocationMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationService {
    void sendLocation(LocationMessage locationMessage, String memberId);
}
