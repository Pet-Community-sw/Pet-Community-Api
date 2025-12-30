package com.example.petapp.domain.profile;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository {

    List<Profile> findList(Member member);
    
    Optional<Profile> find(Long id);

    Profile save(Profile profile);

    void delete(Profile profile);
}
