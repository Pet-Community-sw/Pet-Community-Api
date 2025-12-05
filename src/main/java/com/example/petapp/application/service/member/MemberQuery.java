package com.example.petapp.application.service.member;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.common.exception.NotFoundException;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQuery implements MemberQueryUseCase {

    private final MemberRepository repository;

    @Override
    public Member findOrThrow(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }

    @Override
    public Member findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }

    @Override
    public Member findOrThrowByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new NotFoundException("해당 유저는 없는 유저입니다. 회원가입 해주세요."));
    }
}
