package com.example.petapp.domain.post.normal;

import com.example.petapp.domain.post.normal.model.dto.response.CreatePostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.response.GetPostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.request.PostDto;
import com.example.petapp.domain.post.normal.model.dto.response.PostResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NormalPostService {

    List<PostResponseDto> getPosts(int page, String email);

    CreatePostResponseDto createPost(PostDto createPostDto, String email);

    GetPostResponseDto getPost(Long postId, String email);

    void deletePost(Long postId, String email);

    void updatePost(Long postId, PostDto postDto, String email);
}
