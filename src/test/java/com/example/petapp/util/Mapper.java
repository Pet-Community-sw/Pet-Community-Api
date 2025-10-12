package com.example.petapp.util;

import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.normal.model.dto.request.PostDto;

public class Mapper {
    public static Member createFakeMember() {
        return Member.builder()
                .memberId(1L)
                .name("최선재")
                .email("chltjswo789@naver.com")
                .password("fpdlswj365!")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
    }

    public static Member createFakeMember(Long memberId, String email) {
        return Member.builder()
                .memberId(memberId)
                .name("최선재")
                .email(email)
                .password("fpdlswj365!")
                .phoneNumber("01043557198")
                .memberImageUrl(null)
                .build();
    }

    public static PostDto toPostDto() {

        return PostDto.builder()
                .title("a")
                .content("b")
                .postImageFile(null)
                .build();
    }
}
