package com.example.petapp.domain.memberstatus;

import com.example.petapp.common.base.dto.MessageResponse;
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
    public MessageResponse updateMemberStatus(String email) {
        Member member = queryService.findByMember(email);
        if (inMemoryService.existForeGroundData(member.getId())) {
            inMemoryService.deleteForeGroundData(member.getId());
            return new MessageResponse("background");
        } else {
            inMemoryService.createForeGroundData(member.getId());
            return new MessageResponse("foreground");
        }
    }
}
