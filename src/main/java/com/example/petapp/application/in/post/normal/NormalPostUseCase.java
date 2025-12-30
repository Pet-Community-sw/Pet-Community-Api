package com.example.petapp.application.in.post.normal;

import com.example.petapp.application.in.post.normal.dto.request.PostDto;
import com.example.petapp.application.in.post.normal.dto.response.CreatePostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.GetPostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;

import java.util.List;

public interface NormalPostUseCase {

    List<PostResponseDto> getPosts(int page, Long id);

    CreatePostResponseDto createPost(PostDto createPostDto, Long id);

    GetPostResponseDto getPost(Long postId, Long id);

    void deletePost(Long postId, Long id);

    void updatePost(Long postId, PostDto postDto, Long id);

    List<PostResponseDto> getPostsByMember(Long memberId, int page, Long id);
}
