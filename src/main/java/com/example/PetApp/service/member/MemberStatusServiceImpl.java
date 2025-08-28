package com.example.PetApp.service.member;

import com.example.PetApp.domain.Member;
import com.example.PetApp.query.MemberQueryService;
import com.example.PetApp.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStatusServiceImpl implements MemberStatusService {

    private final StringRedisTemplate stringRedisTemplate;
    private final MemberQueryService memberQueryService;

    @Override
    public void updateMemberStatus(String email) {
        Member member = memberQueryService.findByMember(email);
        stringRedisTemplate.opsForSet().add("foreGroundMembers:", member.getMemberId().toString());
        }
    }
