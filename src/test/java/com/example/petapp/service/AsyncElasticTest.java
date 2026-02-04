package com.example.petapp.service;

import com.example.petapp.application.service.member.MemberSearchService;
import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.domain.member.model.MemberSearch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.Mockito.*;

@SpringBootTest
public class AsyncElasticTest {
    @SpyBean
    private MemberSearchService memberSearchListener;

    @MockBean
    private MemberSearchRepository repository;

    @Test
    public void 재시도_4번_후_recover_호출되어야_한다_성공() {
        //given
        MemberCreateEvent memberCreateEvent = mock(MemberCreateEvent.class);

        //리턴 값이 void인 경우 when.thenThrow 대신 doThrow를 사용
        doThrow(new RuntimeException("Elasticsearch error"))
                .when(repository)
                .save(any(MemberSearch.class));

        //when
        memberSearchListener.create(memberCreateEvent);

        //then
        verify(memberSearchListener, times(4)).create(memberCreateEvent);
    }

}
