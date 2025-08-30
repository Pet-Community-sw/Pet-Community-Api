package com.example.PetApp.domain.post.normal.mapper;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.comment.model.entity.Commentable;
import com.example.PetApp.domain.post.normal.model.entity.NormalPost;
import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.infrastructure.database.shared.embedded.Content;
import com.example.PetApp.domain.comment.model.dto.response.GetCommentsResponseDto;
import com.example.PetApp.domain.post.normal.model.dto.response.GetPostResponseDto;
import com.example.PetApp.domain.post.normal.model.dto.request.PostDto;
import com.example.PetApp.domain.post.normal.model.dto.response.PostResponseDto;
import com.example.PetApp.domain.comment.mapper.CommentMapper;
import com.example.PetApp.common.util.TimeAgoUtil;

import java.util.*;
import java.util.stream.Collectors;

public class NormalPostMapper {

    public static NormalPost toEntity(PostDto postDto, String imageFileName, Member member) {
        return NormalPost.builder()
                .content(new Content(postDto.getTitle(), postDto.getContent()))
                .postImageUrl(imageFileName)
                .member(member)
                .build();
    }

    public static <T extends Post> List<PostResponseDto> toPostListResponseDto(List<T> posts, Map<Long, Long> likeCountMap, Collection<Long> likedPostIds) {
        return posts.stream()
                .map(post -> PostResponseDto.builder()
                        .postId(post.getId())
                        .postImageUrl(post.getPostImageUrl())
                        .memberId(post.getMember().getId())
                        .memberName(post.getMember().getName())
                        .memberImageUrl(post.getMember().getMemberImageUrl())
                        .createdAt(TimeAgoUtil.getTimeAgo(post.getCreatedAt()))
                        .viewCount(post.getViewCount())
                        .likeCount(likeCountMap.getOrDefault(post.getId(), 0L))
                        .title(post.getContent().getTitle())
                        .like(likedPostIds.contains(post.getId()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public static GetPostResponseDto toGetPostResponseDto(Post post,
                                                          Member member,
                                                          Long likeCount,
                                                          boolean isLike) {
        PostResponseDto postResponseDto=PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getContent().getTitle())
                .postImageUrl(post.getPostImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .memberId(post.getMember().getId())
                .memberName(post.getMember().getName())
                .memberImageUrl(post.getMember().getMemberImageUrl())
                .createdAt(TimeAgoUtil.getTimeAgo(post.getCreatedAt()))
                .like(isLike)
                .build();
        List<GetCommentsResponseDto> commentsResponseDtos = CommentMapper.toGetCommentsResponseDtos((Commentable)post, member);

        return GetPostResponseDto.builder()
                .content(post.getContent().getContent())
                .isOwner(post.getMember().equals(member))
                .postResponseDto(postResponseDto)
                .comments(commentsResponseDtos)
                .build();

    }

}
