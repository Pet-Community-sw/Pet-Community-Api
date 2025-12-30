package com.example.petapp.infrastructure.database.jpa.profile;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByMember(Member member);
}

