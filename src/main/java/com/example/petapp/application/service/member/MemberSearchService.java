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
            case CREATE -> create(memberEvent, event.getId());
            case UPDATE -> update(memberEvent, event.getId());
            case DELETE -> delete(memberEvent);
        }
    }

    public void create(MemberEvent event, Long id) {
        MemberSearch document = MemberSearch.builder()
                .memberId(event.getMemberId())
                .memberName(event.getMemberName())
                .memberNameChosung(event.getMemberNameChosung())
                .memberImageUrl(event.getMemberImageUrl())
                .outboxEventId(id)
                .build();
        repository.save(document);
    }

    /**
     * 과거 메시지가 재시도로 인해 늦게 도착할 수 있으므로 이벤트 아이디로 최신 여부를 판단
     */
    public void update(MemberEvent event, Long id) {
        repository.find(event.getMemberId()).ifPresentOrElse(memberSearch -> {
                    if (id > memberSearch.getOutboxEventId()) {
                        repository.save(MemberSearch.builder()
                                .memberId(event.getMemberId())
                                .memberName(event.getMemberName())
                                .memberNameChosung(event.getMemberNameChosung())
                                .memberImageUrl(event.getMemberImageUrl())
                                .outboxEventId(id)
                                .build());
                    } else log.info("최신 이벤트 아님 : eventId : {}", id);
                }, () -> create(event, id)
        );
    }

    public void delete(MemberEvent event) {
        repository.delete(event.getMemberId());
    }
}
