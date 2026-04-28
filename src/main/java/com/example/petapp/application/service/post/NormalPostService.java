package com.example.petapp.application.service.post;

import com.example.petapp.application.in.like.LikeUseCase;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.post.PostUseCase;
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

    private final PostUseCase<NormalPost> postUseCase;
    private final PostRepository<NormalPost> postRepository;
    private final MemberUseCase memberUseCase;
    private final LikeUseCase likeUseCase;
    private final StoragePort storagePort;


    @Override
    public List<PostResponseDto> getPosts(int page, Long id) {
        memberUseCase.findOrThrow(id);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<PostResponseDto> posts = postUseCase.findList(id, pageRequest).getContent();
        NormalPostMapper.toPostListResponseDto(posts);
        return posts;
    }

    @Override
    public List<PostResponseDto> getPostsByMember(Long memberId, int page, Long id) {
        memberUseCase.findOrThrow(memberId);
        memberUseCase.findOrThrow(id);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<PostResponseDto> posts = postUseCase.findListByMember(memberId, id, pageRequest).getContent();
        NormalPostMapper.toPostListResponseDto(posts);
        return posts;
    }

    @Transactional
    @Override
    public GetPostResponseDto getPost(Long postId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        NormalPost normalPost = postUseCase.findOrThrow(postId);
        postRepository.incrementViewCount(normalPost.getId());
        return NormalPostMapper.toGetPostResponseDto(normalPost, member, likeUseCase.countByPost(normalPost), likeUseCase.exist(normalPost, member));
    }

    @Transactional
    @Override
    public CreatePostResponseDto createPost(PostDto createPostDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        String imageFileName = storagePort.uploadFile(createPostDto.getPostImageFile(), FileKind.POST);
        NormalPost normalPost = NormalPostMapper.toEntity(createPostDto, imageFileName, member);
        NormalPost savedPost = postRepository.save(normalPost);
        return new CreatePostResponseDto(savedPost.getId());
    }

    @Transactional
    @Override
    public void deletePost(Long postId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        NormalPost normalPost = postUseCase.findOrThrow(postId);
        normalPost.validateMember(member);
        postRepository.delete(postId);
    }

    @Transactional
    @Override
    public void updatePost(Long postId, PostDto updatePostDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        NormalPost normalPost = postUseCase.findOrThrow(postId);
        normalPost.validateMember(member);
        normalPost.updateNormalPost(storagePort.uploadFile(updatePostDto.getPostImageFile(), FileKind.POST), updatePostDto.getTitle(), updatePostDto.getContent());
    }
}

