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

    /**
     * 검색창에서 입력값이 바뀔 때마다 자동완성 목적.
     */
    @Override
    public List<MemberSearchResponseDto> autoComplete(String keyword) {

        BoolQueryBuilder queryBuilder = boolQuery()
                .should(termQuery("memberName.keyword", keyword).boost(10f))
                .should(matchQuery("memberName.prefix", keyword).boost(6f));// matchQuery는 분석을 거친 뒤 매칭

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<MemberSearch> hits = operations.search(query, MemberSearch.class);


        return hits.getSearchHits().stream()
                .map(this::toObject)
                .toList();
    }

    /**
     * 검색 결과 전체 보기 목적.
     * 페이지네이션, 중간 포함 매칭 추가.
     * 사용자가 검색 했을 때 api호출
     */
    @Override
    public List<MemberSearchResponseDto> search(String keyword, int page) {
        if (keyword.length() < 2) return List.of();

        BoolQueryBuilder queryBuilder = boolQuery()
                .should(termQuery("memberName.keyword", keyword).boost(10f))//keyword는 분석을 안거치므로 termQuery
                .should(matchQuery("memberName.prefix", keyword).boost(6f))//앞부분 매칭
                .should(matchQuery("memberName.contains", keyword).boost(2f));//중간 포함

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(page, 10))
                .build();

        SearchHits<MemberSearch> hits = operations.search(query, MemberSearch.class);

        return hits.getSearchHits().stream()
                .map(this::toObject)
                .toList();
    }

    //todo : 캐싱
    private MemberSearchResponseDto toObject(SearchHit<MemberSearch> hit) {
        MemberSearch memberSearch = hit.getContent();
        return MemberSearchResponseDto.builder()
                .memberId(memberSearch.getMemberId())
                .memberName(memberSearch.getMemberName())
                .memberImageUrl(memberSearch.getMemberImageUrl())
                .build();
    }
}
