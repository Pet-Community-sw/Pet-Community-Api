package com.example.petapp.infrastructure.database.jpa.member;

import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final JpaMemberRepository repository;

    @Override
    public void delete(Member member) {
        repository.delete(member);
    }

    @Override
    public Member save(Member member) {
        return repository.save(member);
    }

    @Override
    public boolean exist(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<Member> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<String> findAllByIds(List<Long> ids) {
        return repository.findNamesByIds(ids);
    }
}
