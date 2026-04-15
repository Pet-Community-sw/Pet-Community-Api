package com.example.petapp.domain.profile;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProfileRepository {

    List<Profile> findList(Member member);

    List<Profile> findAllByIds(Set<Long> ids);
    
    Optional<Profile> find(Long id);

    Profile save(Profile profile);

    void delete(Profile profile);
}
