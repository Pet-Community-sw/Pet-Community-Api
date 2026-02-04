package com.example.petapp.application.in.member;

import com.example.petapp.application.in.member.object.MemberEvent;

public interface MemberSearchUseCase {
    void handle(MemberEvent event);
}
