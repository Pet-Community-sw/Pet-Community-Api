package com.example.petapp.application.in.post.normal.mapper;

import com.example.petapp.application.common.TimeUtil;
import com.example.petapp.application.in.comment.dto.response.GetCommentsResponseDto;
import com.example.petapp.application.in.comment.mapper.CommentMapper;
import com.example.petapp.application.in.post.normal.dto.request.PostDto;
import com.example.petapp.application.in.post.normal.dto.response.GetPostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.domain.comment.model.Commentable;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Content;
import com.example.petapp.domain.post.model.NormalPost;
import com.example.petapp.domain.post.model.Post;

import java.util.List;

public class NormalPostMapper {

    public static NormalPost toEntity(PostDto postDto, String imageFileName, Member member) {
        return NormalPost.builder()
                .content(new Content(postDto.getTitle(), postDto.getContent()))
                .postImageUrl(imageFileName)
                .member(member)
                .build();
    }

    public static void toPostListResponseDto(List<PostResponseDto> posts) {
        posts.forEach(dto -> dto.setPostBeforeTime(TimeUtil.getTimeAgo(dto.getCreatedAt())));
    }

    public static GetPostResponseDto toGetPostResponseDto(Post post,
                                                          Member member,
                                                          Long likeCount,
                                                          boolean isLike) {
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getContent().getTitle())
                .postImageUrl(post.getPostImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .memberId(post.getMember().getId())
                .memberName(post.getMember().getName())
                .memberImageUrl(post.getMember().getMemberImageUrl())
                .postBeforeTime(TimeUtil.getTimeAgo(post.getCreatedAt()))
                .like(isLike)
                .build();
        List<GetCommentsResponseDto> commentsResponseDtos = CommentMapper.toGetCommentsResponseDtos((Commentable) post, member);
        return GetPostResponseDto.builder()
                .content(post.getContent().getContent())
                .isOwner(post.getMember().equals(member))
                .postResponseDto(postResponseDto)
                .comments(commentsResponseDtos)
                .build();
    }
}
