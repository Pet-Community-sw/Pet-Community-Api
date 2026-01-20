package com.example.petapp.infrastructure.event.member;

import com.example.petapp.application.in.member.object.MemberCreateEvent;
import com.example.petapp.application.in.member.object.MemberDeleteEvent;
import com.example.petapp.application.in.member.object.MemberUpdateEvent;
import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.domain.member.model.MemberSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberSearchListener {

    private final MemberSearchRepository repository;

    @Async("elasticExecutor")
    @Retryable(
            maxAttempts = 4,// 최대 재시도 횟수(기본값 3)
            backoff = @Backoff(delay = 2000, multiplier = 2.0, random = true)// 재시도 간격
    )
    //DB 커밋 이후에 이벤트 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createHandle(MemberCreateEvent event) {
        MemberSearch document = MemberSearch.builder()
                .memberId(event.memberId())
                .memberName(event.memberName())
                .memberImageUrl(event.memberImageUrl())
                .build();
        repository.save(document);
    }

    @Async("elasticExecutor")
    @Retryable(
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2.0, random = true)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateHandle(MemberUpdateEvent event) {
        MemberSearch document = MemberSearch.builder()
                .memberId(event.memberId())
                .memberName(event.memberName())
                .memberImageUrl(event.memberImageUrl())
                .build();
        repository.save(document);
    }

    @Async("elasticExecutor")
    @Retryable(
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2.0, random = true)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteHandle(MemberDeleteEvent event) {
        repository.delete(event.memberId());
    }

    @Recover
    public void createRecover(Exception e, MemberCreateEvent event) {
        log.error("ElasticSearch 저장 실패 memberId: {}, error: {}", event.memberId(), e.getMessage());
    }

    @Recover
    public void updateRecover(Exception e, MemberUpdateEvent event) {
        log.error("ElasticSearch 수정 실패 memberId: {}, error: {}", event.memberId(), e.getMessage());
    }

    @Recover
    public void deleteRecover(Exception e, MemberDeleteEvent event) {
        log.error("ElasticSearch 삭제 실패 memberId: {}, error: {}", event.memberId(), e.getMessage());
    }
}
