package com.example.PetApp.domain.like.mapper;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.like.model.entity.Like;
import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.like.model.dto.request.LikeListDto;
import com.example.PetApp.domain.like.model.dto.response.LikeResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class LikeMapper {

    public static Like toEntity(Member member, Post post) {
        return Like.builder()
                .member(member)
                .post(post)
                .build();
    }

    public static List<LikeListDto> toLikeListDto(List<Like> likes) {
        return likes.stream()
                .map(like -> LikeListDto.builder()
                        .memberId(like.getMember().getMemberId())
                        .memberName(like.getMember().getName())
                        .memberImageUrl(like.getMember().getMemberImageUrl())
                        .build()
                ).collect(Collectors.toList());
    }

    public static LikeResponseDto toLikeResponseDto(List<Like> likes) {
        List<LikeListDto> likeListDto = toLikeListDto(likes);
        return LikeResponseDto.builder()
                .likeListDtos(likeListDto)
                .likeCount((long) likeListDto.size())
                .build();
    }

}
