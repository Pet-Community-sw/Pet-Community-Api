package com.example.petapp.domain.like.mapper;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.like.model.entity.Like;
import com.example.petapp.domain.post.common.Post;
import com.example.petapp.domain.like.model.dto.request.LikeListDto;
import com.example.petapp.domain.like.model.dto.response.LikeResponseDto;

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
                        .memberId(like.getMember().getId())
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
