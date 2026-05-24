package com.example.petapp.application.usecase.member;

import com.example.petapp.domain.member.model.Member;

import java.util.List;

public interface MemberQueryUseCase {
    Member findOrThrow(String email);

    Member findOrThrow(Long id);

    Member findOrThrowByPhoneNumber(String phoneNumber);

    List<String> findNamesOrThrowByIds(List<Long> ids);
}
