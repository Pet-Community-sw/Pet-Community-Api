package com.example.petapp.infrastructure.database.elasticsearch;

import com.example.petapp.domain.member.model.MemberSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticMemberSearchRepository extends ElasticsearchRepository<MemberSearch, Long> {
}
