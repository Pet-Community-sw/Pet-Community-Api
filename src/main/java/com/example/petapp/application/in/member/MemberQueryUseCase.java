package com.example.petapp.application.in.member;

import com.example.petapp.domain.member.model.Member;

public interface MemberQueryUseCase {
    Member findOrThrow(String email);

    Member findOrThrow(Long id);

    Member findOrThrowByPhoneNumber(String phoneNumber);
}
