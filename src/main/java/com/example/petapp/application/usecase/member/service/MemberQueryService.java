package com.example.petapp.application.usecase.member.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.member.MemberQueryUseCase;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberQueryService implements MemberQueryUseCase {

    private final MemberRepository repository;

    @Override
    public Member findOrThrow(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 유저는 없습니다."));
    }

    @Override
    public Member findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 유저는 없습니다."));
    }

    @Override
    public Member findOrThrowByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 유저는 없는 유저입니다. 회원가입 해주세요."));
    }

    @Override
    public List<String> findNamesOrThrowByIds(List<Long> ids) {
        return repository.findAllByIds(ids);
    }
}
