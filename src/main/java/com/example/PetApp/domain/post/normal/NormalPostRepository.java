package com.example.PetApp.domain.post.normal;

import com.example.PetApp.domain.post.normal.model.entity.NormalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalPostRepository extends JpaRepository<NormalPost, Long> {

}
