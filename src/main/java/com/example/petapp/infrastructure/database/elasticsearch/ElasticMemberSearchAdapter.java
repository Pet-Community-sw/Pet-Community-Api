package com.example.petapp.infrastructure.database.elasticsearch;

import com.example.petapp.application.common.NameChosungUtil;
import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.out.MemberSearchPort;
import com.example.petapp.domain.member.model.MemberSearch;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
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
                .should(termQuery("memberName", keyword).boost(10f))
                .should(matchQuery("memberName.prefix", keyword).boost(6f));// matchQuery는 분석을 거친 뒤 매칭

        // 초성 검색어일 경우 초성 필드 검색
        if (NameChosungUtil.isChosung(keyword)) {
            queryBuilder.should(termQuery("memberNameChosung", keyword).boost(10f));
            queryBuilder.should(matchQuery("memberNameChosung.prefix", keyword).boost(8f));
        }

        NativeSearchQuery query = buildQuery(0, queryBuilder);

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
        BoolQueryBuilder queryBuilder = boolQuery()
                .should(termQuery("memberName", keyword).boost(10f))//keyword는 분석을 안거치므로 termQuery
                .should(matchQuery("memberName.prefix", keyword).boost(8f));//앞부분 매칭


        /**
         * 키워드가 2글자 이상일 경우에만 중간 포함 매칭
         */
        if (keyword.length() > 1) {
            queryBuilder.should(matchQuery("memberName.contains", keyword).boost(6f));//중간 포함
        }

        if (NameChosungUtil.isChosung(keyword)) {
            queryBuilder.should(termQuery("memberNameChosung", keyword).boost(10f));
            queryBuilder.should(matchQuery("memberNameChosung.prefix", keyword).boost(8f));
        }

        SearchHits<MemberSearch> hits = operations.search(buildQuery(page, queryBuilder), MemberSearch.class);

        /**
         * 키워드가 영문일 경우에만 오타 교정
         * 0페이지에서 오타교정 (사용자는 오타인지 첫 페이지에서 확인할 수 있기 때문)
         * 검색 결과가 3개 이하일 때만 오타교정 (결과가 많으면 오타가 아닐 확률이 높기 때문)
         */
        if (page == 0 && hits.getTotalHits() <= 3 && keyword.matches("^[A-Za-z]+$")) {
            queryBuilder.should(matchQuery("memberName.fuzzy", keyword)
                    .fuzziness(Fuzziness.AUTO)
                    .prefixLength(1)
                    .maxExpansions(10) //후보군 최대 개수
                    .boost(1f)
            );

            hits = operations.search(buildQuery(page, queryBuilder), MemberSearch.class);
        }


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

    private NativeSearchQuery buildQuery(int page, BoolQueryBuilder queryBuilder) {
        return new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(page, 10))
                .build();
    }
}
