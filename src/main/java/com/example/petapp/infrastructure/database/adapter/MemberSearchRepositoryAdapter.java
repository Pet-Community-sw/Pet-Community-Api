package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.domain.member.model.MemberSearch;
import com.example.petapp.infrastructure.database.elasticsearch.ElasticMemberSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberSearchRepositoryAdapter implements MemberSearchRepository {

    private final ElasticMemberSearchRepository repository;

    @Override
    public void save(MemberSearch document) {
        repository.save(document);
    }

    @Override
    public void delete(Long id) {
        log.info("삭제 요청");

        repository.deleteById(id);
    }
}
