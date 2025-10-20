package com.example.petapp.domain.comment.mapper;

import com.example.petapp.common.base.util.TimeUtil;
import com.example.petapp.domain.comment.model.dto.request.CommentDto;
import com.example.petapp.domain.comment.model.dto.response.GetCommentsResponseDto;
import com.example.petapp.domain.comment.model.entity.Comment;
import com.example.petapp.domain.comment.model.entity.Commentable;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.common.Post;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static List<GetCommentsResponseDto> toGetCommentsResponseDtos(Commentable post, Member member) {
        return getCommentsResponseDtos(post.getComments(), member);
    }

    public static Comment toEntity(CommentDto commentDto, Post post, Member member) {
        return Comment.builder()
                .content(commentDto.getContent())
                .post(post)
                .member(member)
                .build();
    }

    @NotNull
    private static List<GetCommentsResponseDto> getCommentsResponseDtos(List<Comment> comments, Member member) {
        return comments.stream().map(
                comment -> new GetCommentsResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getMember().getId(),
                        comment.getMember().getName(),
                        comment.getMember().getMemberImageUrl(),
                        TimeUtil.getTimeAgo(comment.getCreatedAt()),
                        comment.getMember().equals(member)
                )
        ).collect(Collectors.toList());
    }
}
