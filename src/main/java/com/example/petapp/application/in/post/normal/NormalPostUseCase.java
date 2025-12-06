package com.example.petapp.application.in.post.normal;

import com.example.petapp.application.in.post.normal.dto.request.PostDto;
import com.example.petapp.application.in.post.normal.dto.response.CreatePostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.GetPostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;

import java.util.List;

public interface NormalPostUseCase {

    List<PostResponseDto> getPosts(int page, String email);

    CreatePostResponseDto createPost(PostDto createPostDto, String email);

    GetPostResponseDto getPost(Long postId, String email);

    void deletePost(Long postId, String email);

    void updatePost(Long postId, PostDto postDto, String email);

    List<PostResponseDto> getPostsByMember(Long memberId, int page, String email);
}
