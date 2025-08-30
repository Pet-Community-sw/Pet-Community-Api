package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.service.query.QueryService;
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
