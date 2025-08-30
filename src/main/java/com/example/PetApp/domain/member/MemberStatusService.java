package com.example.PetApp.domain.member;

import org.springframework.stereotype.Service;

@Service
public interface MemberStatusService {

    void updateMemberStatus(String string);
}
