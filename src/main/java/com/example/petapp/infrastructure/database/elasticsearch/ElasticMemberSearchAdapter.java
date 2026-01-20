package com.example.petapp.infrastructure.database.elasticsearch;

import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.out.MemberSearchPort;
import com.example.petapp.domain.member.model.MemberSearch;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Component
@RequiredArgsConstructor
public class ElasticMemberSearchAdapter implements MemberSearchPort {

    //Spring Data Elasticsearch가 제공하는 Es에 검색 요청 보내는 객체
    private final ElasticsearchOperations operations;

    @Override
    public List<MemberSearchResponseDto> autoComplete(String keyword, int size) {
        String word = wordHandle(keyword);

        BoolQueryBuilder queryBuilder = boolQuery()
                .should(matchQuery("memberName.prefix", word).boost(6f))
                .should(prefixQuery("memberName", word).boost(2f))
                .minimumShouldMatch(1);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(0, size))
                .build();

        SearchHits<MemberSearch> hits = operations.search(query, MemberSearch.class);

        return hits.getSearchHits().stream()
                .map(this::toObject)
                .toList();
    }

    @Override
    public List<MemberSearchResponseDto> search(String keyword, int page, int size) {
        String word = wordHandle(keyword);
        if (word.length() < 2) return List.of();

        BoolQueryBuilder queryBuilder = boolQuery()
                .should(termQuery("memberName.keyword", word).boost(10f))   // 정확히 같으면 최상단
                .should(matchQuery("memberName.prefix", word).boost(6f))    // 앞부분 매칭
                .should(matchQuery("memberName.contains", word).boost(2f))  // 중간 포함(보조)
                .should(matchQuery("memberName", word).boost(1f))           // 기본 검색
                .minimumShouldMatch(1);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<MemberSearch> hits = operations.search(query, MemberSearch.class);

        return hits.getSearchHits().stream()
                .map(this::toObject)
                .toList();
    }

    private MemberSearchResponseDto toObject(SearchHit<MemberSearch> hit) {
        MemberSearch memberSearch = hit.getContent();
        return MemberSearchResponseDto.builder()
                .memberId(memberSearch.getMemberId())
                .memberName(memberSearch.getMemberName())
                .memberImageUrl(memberSearch.getMemberImageUrl())
                .build();
    }

    private String wordHandle(String keyword) {
        return keyword.replaceAll("\\s+", "").toLowerCase();
    }
}
