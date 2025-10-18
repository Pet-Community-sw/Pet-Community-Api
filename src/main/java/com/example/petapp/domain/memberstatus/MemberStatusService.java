package com.example.petapp.domain.memberstatus;

import com.example.petapp.common.base.dto.MessageResponse;
import org.springframework.stereotype.Service;

@Service
public interface MemberStatusService {

    MessageResponse updateMemberStatus(String string);
}
