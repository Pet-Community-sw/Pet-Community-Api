package com.example.petapp.domain.memberstatus;

import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStatusServiceImpl implements MemberStatusService {

    private final QueryService queryService;
    private final InMemoryService inMemoryService;

    @Override
    public void updateMemberStatus(String email) {
        Member member = queryService.findByMember(email);
        inMemoryService.createForeGroundData(member.getId());
    }
}
