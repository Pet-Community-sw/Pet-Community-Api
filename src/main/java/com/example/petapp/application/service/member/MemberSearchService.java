package com.example.petapp.application.service.member;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.member.MemberSearchUseCase;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.domain.member.model.MemberSearch;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberSearchService implements MemberSearchUseCase {

    private final MemberSearchRepository repository;
    private final JsonUtil jsonUtil;

    @Override
    public void handle(OutboxEvent event) {
        MemberEvent memberEvent = jsonUtil.fromJson(event.getPayload(), MemberEvent.class);
        switch (memberEvent.getMethodType()) {
            case CREATE -> create(memberEvent);
            case UPDATE -> update(memberEvent);
            case DELETE -> delete(memberEvent);
        }
    }

    public void create(MemberEvent event) {
        MemberSearch document = MemberSearch.builder()
                .memberId(event.getMemberId())
                .memberName(event.getMemberName())
                .memberNameChosung(event.getMemberNameChosung())
                .memberImageUrl(event.getMemberImageUrl())
                .build();
        repository.save(document);
    }

    public void update(MemberEvent event) {
        MemberSearch document = MemberSearch.builder()
                .memberId(event.getMemberId())
                .memberName(event.getMemberName())
                .memberNameChosung(event.getMemberNameChosung())
                .memberImageUrl(event.getMemberImageUrl())
                .build();
        repository.save(document);
    }

    public void delete(MemberEvent event) {
        repository.delete(event.getMemberId());
    }
}
