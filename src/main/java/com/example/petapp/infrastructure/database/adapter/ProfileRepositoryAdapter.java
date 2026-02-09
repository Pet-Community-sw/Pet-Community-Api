package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.ProfileRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.infrastructure.database.jpa.profile.JpaProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepository {

    private final JpaProfileRepository repository;

    @Override
    public List<Profile> findList(Member member) {
        return repository.findByMember(member);
    }

    @Override
    public Optional<Profile> find(Long id) {
        return repository.findById(id);
    }

    @Override
    public Profile save(Profile profile) {
        return repository.save(profile);
    }

    @Override
    public void delete(Profile profile) {
        repository.delete(profile);
    }
}
