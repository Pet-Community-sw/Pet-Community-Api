package com.example.petapp.domain.post.normal;

import com.example.petapp.domain.post.normal.model.entity.NormalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalPostRepository extends JpaRepository<NormalPost, Long> {

}
