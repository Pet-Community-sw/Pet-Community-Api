package com.example.petapp.application.service.member;

import com.example.petapp.application.in.member.MemberSearchUseCase;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.domain.member.MemberSearchRepository;
import com.example.petapp.domain.member.model.MemberSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberSearchService implements MemberSearchUseCase {

    private final MemberSearchRepository repository;

    @Override
    public void handle(MemberEvent event) {
        switch (event.getMethodType()) {
            case CREATE -> create(event);
            case UPDATE -> update(event);
            case DELETE -> delete(event);
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
