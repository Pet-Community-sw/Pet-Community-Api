package com.example.petapp.domain.member;

import com.example.petapp.domain.member.model.MemberSearch;

import java.util.Optional;

public interface MemberSearchRepository {
    Optional<MemberSearch> find(Long id);

    void save(MemberSearch document);

    void delete(Long id);
}