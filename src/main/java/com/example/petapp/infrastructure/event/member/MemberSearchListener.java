package com.example.petapp.infrastructure.event.member;

import com.example.petapp.application.in.member.object.MemberSearchEvent;
import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.domain.member.model.MemberSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberSearchListener {

    private final MemberSearchRepository repository;

    @Async("elasticExecutor")
    //DB 커밋 이후에 이벤트 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberSearchEvent event) {
        MemberSearch document = MemberSearch.builder()
                .memberId(event.memberId())
                .memberName(event.memberName())
                .memberImageUrl(event.memberImageUrl())
                .build();
        repository.save(document);
    }
}
