package com.example.petapp.domain.memberstatus;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.out.cache.AppOnlineCachePort;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.interfaces.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStatusServiceImpl implements MemberStatusService {

    private final MemberQueryUseCase useCase;
    private final AppOnlineCachePort port;

    @Override
    public MessageResponse updateMemberStatus(String email) {
        Member member = useCase.findOrThrow(email);
        if (port.exist(member.getId())) {
            port.delete(member.getId());
            return new MessageResponse("background");
        } else {
            port.create(member.getId());
            return new MessageResponse("foreground");
        }
    }
}
