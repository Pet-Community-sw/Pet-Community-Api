package com.example.petapp.infrastructure.database.elasticsearch;

import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.domain.member.model.MemberSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberSearchRepositoryImpl implements MemberSearchRepository {

    private final ElasticMemberSearchRepository repository;
    
    @Override
    public void save(MemberSearch document) {
        repository.save(document);
    }
}
