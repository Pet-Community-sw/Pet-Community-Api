package com.example.petapp.infrastructure.database.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.petapp.application.common.NameChosungUtil;
import com.example.petapp.application.common.PagePolicy;
import com.example.petapp.application.out.MemberSearchPort;
import com.example.petapp.application.usecase.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.domain.member.model.MemberSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticMemberSearchAdapter implements MemberSearchPort {


    private static final int FIRST_PAGE_INDEX = 0;
    
    private static final float EXACT_NAME_BOOST = 10f;
    private static final float PREFIX_BOOST = 6f;
    private static final float CHOSUNG_EXACT_BOOST = 10f;
    private static final float CHOSUNG_PREFIX_BOOST = 8f;
    //Spring Data Elasticsearch가 제공하는 Es에 검색 요청 보내는 객체
    private final ElasticsearchOperations operations;

    /**
     * 검색창에서 입력값이 바뀔 때마다 자동완성 목적.
     */
    @Override
    public List<MemberSearchResponseDto> searchSuggestions(String keyword) {

        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(termQuery("memberName", keyword, EXACT_NAME_BOOST));
        shouldQueries.add(termQuery("memberName.prefix", keyword, PREFIX_BOOST));// matchQuery는 분석을 거친 뒤 매칭

        // 초성 검색어일 경우 초성 필드 검색
        if (NameChosungUtil.isChosung(keyword)) {
            shouldQueries.add(termQuery("memberNameChosung", keyword, CHOSUNG_EXACT_BOOST));
            shouldQueries.add(termQuery("memberNameChosung.prefix", keyword, CHOSUNG_PREFIX_BOOST));
        }

        NativeQuery query = buildQuery(FIRST_PAGE_INDEX, shouldQueries);

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
        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(termQuery("memberName", keyword, EXACT_NAME_BOOST));//keyword는 분석을 안거치므로 termQuery
        shouldQueries.add(termQuery("memberName.prefix", keyword, PREFIX_BOOST));//앞부분 매칭


        /**
         * 키워드가 2글자 이상일 경우에만 중간 포함 매칭
         */
        if (keyword.length() >= 2) {
            shouldQueries.add(Query.of(q -> q.match(match -> match
                    .field("memberName.contains")
                    .query(keyword)
                    .boost(6f))));//중간 포함
        }

        if (NameChosungUtil.isChosung(keyword)) {
            shouldQueries.add(termQuery("memberNameChosung", keyword, CHOSUNG_EXACT_BOOST));
            shouldQueries.add(termQuery("memberNameChosung.prefix", keyword, CHOSUNG_PREFIX_BOOST));
        }

        SearchHits<MemberSearch> hits = operations.search(buildQuery(page, shouldQueries), MemberSearch.class);

        /**
         * 키워드가 영문일 경우에만 오타 교정
         * 0페이지에서 오타교정 (사용자는 오타인지 첫 페이지에서 확인할 수 있기 때문)
         * 검색 결과가 3개 이하일 때만 오타교정 (결과가 많으면 오타가 아닐 확률이 높기 때문)
         */
        if (page == FIRST_PAGE_INDEX && hits.getTotalHits() <= 3 && keyword.matches("^[A-Za-z]+$")) {
            shouldQueries.add(Query.of(q -> q.fuzzy(fuzzy -> fuzzy
                    .field("memberName")
                    .value(keyword)
                    .fuzziness("AUTO")
                    .prefixLength(1)
                    .maxExpansions(10) //후보군 최대 개수
                    .boost(1f))));

            hits = operations.search(buildQuery(page, shouldQueries), MemberSearch.class);
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

    private NativeQuery buildQuery(int page, List<Query> shouldQueries) {
        Query query = Query.of(q -> q.bool(bool -> bool
                        .minimumShouldMatch("1")
                        .should(shouldQueries)
                        .filter(Query.of(filter -> filter.term(term -> term
                                                .field("isDeleted")
                                                .value(FieldValue.of(false))
                                        )
                                )
                        )
                )
        );//소프트 딜리트 필터링

        return NativeQuery.builder()
                .withQuery(query)
                .withPageable(PageRequest.of(page, PagePolicy.DEFAULT_PAGE_SIZE))
                .build();
    }

    private Query termQuery(String field, String value, float boost) {
        return Query.of(q -> q.term(term -> term
                .field(field)
                .value(FieldValue.of(value))
                .boost(boost)));
    }

}
