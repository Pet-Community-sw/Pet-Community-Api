package com.example.PetApp.domain.profile;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.profile.model.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findByMember(Member member);

    Long countByMember(Member member);

}
