package com.example.petapp.application.in.member.object;

public record MemberUpdateEvent(Long memberId, String memberName, String memberNameChosung, String memberImageUrl) {
}
