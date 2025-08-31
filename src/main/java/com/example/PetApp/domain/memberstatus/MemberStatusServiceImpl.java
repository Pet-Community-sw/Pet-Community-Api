package com.example.PetApp.domain.memberstatus;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStatusServiceImpl implements MemberStatusService {

    private final StringRedisTemplate stringRedisTemplate;
    private final QueryService queryService;

    @Override
    public void updateMemberStatus(String email) {
        Member member = queryService.findByMember(email);
        stringRedisTemplate.opsForSet().add("foreGroundMembers:", member.getId().toString());
        }
    }
