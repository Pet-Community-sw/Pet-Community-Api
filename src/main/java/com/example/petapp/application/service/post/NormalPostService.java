package com.example.petapp.application.service.post;

import com.example.petapp.application.in.like.LikeQueryUseCase;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.application.in.post.normal.NormalPostUseCase;
import com.example.petapp.application.in.post.normal.dto.request.PostDto;
import com.example.petapp.application.in.post.normal.dto.response.CreatePostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.GetPostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.application.in.post.normal.mapper.NormalPostMapper;
import com.example.petapp.application.out.StoragePort;
import com.example.petapp.domain.file.FileKind;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.model.NormalPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NormalPostService implements NormalPostUseCase {

    private final PostQueryUseCase<NormalPost> postQueryUseCase;
    private final PostRepository<NormalPost> postRepository;
    private final MemberQueryUseCase memberQueryUseCase;
    private final LikeQueryUseCase likeQueryUseCase;
    private final StoragePort storagePort;
    

    @Transactional(readOnly = true)
    @Override
    public List<PostResponseDto> getPosts(int page, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<NormalPost> normalPosts = postQueryUseCase.findList(pageRequest).getContent();
        return NormalPostMapper.toPostListResponseDto(normalPosts, likeQueryUseCase.getCountMap(normalPosts), member);
    }

    @Override
    public List<PostResponseDto> getPostsByMember(Long memberId, int page, String email) {
        memberQueryUseCase.findOrThrow(memberId);
        Member member = memberQueryUseCase.findOrThrow(email);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<NormalPost> normalPosts = postQueryUseCase.findList(memberId, pageRequest).getContent();
        return NormalPostMapper.toPostListResponseDto(normalPosts, likeQueryUseCase.getCountMap(normalPosts), member);
    }

    @Transactional
    @Override
    public GetPostResponseDto getPost(Long postId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        NormalPost normalPost = postQueryUseCase.findOrThrow(postId);
        postRepository.incrementViewCount(normalPost.getId());
        return NormalPostMapper.toGetPostResponseDto(normalPost, member, likeQueryUseCase.countByPost(normalPost), likeQueryUseCase.exist(normalPost, member));
    }

    @Transactional
    @Override
    public CreatePostResponseDto createPost(PostDto createPostDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        String imageFileName = storagePort.uploadFile(createPostDto.getPostImageFile(), FileKind.POST);
        NormalPost normalPost = NormalPostMapper.toEntity(createPostDto, imageFileName, member);
        NormalPost savedPost = postRepository.save(normalPost);
        return new CreatePostResponseDto(savedPost.getId());
    }

    @Transactional
    @Override
    public void deletePost(Long postId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        NormalPost normalPost = postQueryUseCase.findOrThrow(postId);
        normalPost.validateMember(member);
        postRepository.delete(postId);
    }

    @Transactional
    @Override
    public void updatePost(Long postId, PostDto updatePostDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        NormalPost normalPost = postQueryUseCase.findOrThrow(postId);
        normalPost.validateMember(member);
        normalPost.updateNormalPost(storagePort.uploadFile(updatePostDto.getPostImageFile(), FileKind.POST), updatePostDto.getTitle(), updatePostDto.getContent());
    }
}

