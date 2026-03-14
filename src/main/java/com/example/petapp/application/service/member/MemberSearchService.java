package com.example.petapp.application.service.member;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.member.MemberSearchUseCase;
import com.example.petapp.application.in.member.mapper.MemberMapper;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.infrastructure.mq.consumer.OutboxMessage;
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
    public void handle(OutboxMessage outboxMessage) {
        MemberEvent memberEvent = jsonUtil.fromJson(outboxMessage.getPayload(), MemberEvent.class);
        Long id = outboxMessage.getId();
        switch (memberEvent.getMethodType()) {
            case CREATE -> create(memberEvent, id);
            case UPDATE -> update(memberEvent, id);
            case DELETE -> delete(memberEvent, id);
        }
    }

    public void create(MemberEvent event, Long id) {
        repository.find(event.getMemberId()).ifPresentOrElse(memberSearch -> {
                    /**
                     * 이미 존재하는 경우는 순서가 뒤바뀐 경우임
                     * 1) update 이벤트가 먼저 도착한 경우 -> update이벤트는 create보다 항상 id가 높으므로 무시
                     * 2) 동일한 create 이벤트가 중복 도착한 경우 -> 멱등성이 보장되므로 무시
                     * 3) delete 이벤트가 먼저 도착한 경우 -> delete이벤트는 create보다 항상 id가 높으므로 무시
                     */

                }, () -> repository.save(MemberMapper.toSearchEntity(event, id))
        );
    }

    /**
     * 과거 메시지가 재시도로 인해 늦게 도착할 수 있으므로 이벤트 아이디로 최신 여부를 판단
     * outboxEventId가 같은값이 들어와도 문제 x -> 멱등성 보장
     */
    public void update(MemberEvent event, Long id) {
        repository.find(event.getMemberId()).ifPresentOrElse(memberSearch -> {
                    if (memberSearch.isDeleted()) return;//삭제된 회원은 제외
                    if (id > memberSearch.getOutboxEventId()) repository.save(MemberMapper.toSearchEntity(event, id));
                }, () -> repository.save(MemberMapper.toSearchEntity(event, id))
        );
    }

    public void delete(MemberEvent event, Long id) {
        repository.find(event.getMemberId()).ifPresentOrElse(memberSearch -> {
                    if (id > memberSearch.getOutboxEventId()) repository.save(MemberMapper.toDeleteSearchEntity(event, id));
                }, () -> repository.save(MemberMapper.toDeleteSearchEntity(event, id))//행이 없어도 순서 유지 위해 삭제 이벤트 저장
        );
    }
}
