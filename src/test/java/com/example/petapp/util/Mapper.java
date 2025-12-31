//package com.example.petapp.util;
//
//import com.example.petapp.application.in.post.normal.dto.request.PostDto;
//import com.example.petapp.domain.member.model.Member;
//
//public class Mapper {
//    public static Member createFakeMember() {
//        return Member.builder()
//                .id(1L)
//                .name("최선재")
//                .email("chltjswo789@naver.com")
//                .password("fpdlswj365!")
//                .phoneNumber("01043557198")
//                .memberImageUrl(null)
//                .build();
//    }
//
//    public static Member createFakeMember(Long id, String email) {
//        return Member.builder()
//                .id(id)
//                .name("최선재")
//                .email(email)
//                .password("fpdlswj365!")
//                .phoneNumber("01043557198")
//                .memberImageUrl(null)
//                .build();
//    }
//
//    public static PostDto toPostDto() {
//
//        return PostDto.builder()
//                .title("a")
//                .content("b")
//                .postImageFile(null)
//                .build();
//    }
//}
