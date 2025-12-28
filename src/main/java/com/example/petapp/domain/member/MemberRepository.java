package com.example.petapp.domain.member;


import com.example.petapp.domain.member.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    void delete(Member member);

    Member save(Member member);

    boolean exist(String email);

    Optional<Member> find(Long id);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    List<String> findAllByIds(List<Long> ids);
}
