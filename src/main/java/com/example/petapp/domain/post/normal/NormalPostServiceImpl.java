package com.example.petapp.domain.post.normal;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.common.base.util.imagefile.FileImageKind;
import com.example.petapp.common.base.util.imagefile.FileUploadUtil;
import com.example.petapp.domain.like.LikeRepository;
import com.example.petapp.domain.like.LikeService;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.normal.mapper.NormalPostMapper;
import com.example.petapp.domain.post.normal.model.dto.request.PostDto;
import com.example.petapp.domain.post.normal.model.dto.response.CreatePostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.response.GetPostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.response.PostResponseDto;
import com.example.petapp.domain.post.normal.model.entity.NormalPost;
import com.example.petapp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NormalPostServiceImpl implements NormalPostService {

    private final NormalPostRepository normalPostRepository;
    private final LikeRepository likeRepository;
    private final LikeService likeService;
    private final QueryService queryService;
    private final MemberQueryUseCase memberQueryUseCase;

    @Value("${spring.dog.post.image.upload}")
    private String postUploadDir;

    @Transactional(readOnly = true)
    @Override
    public List<PostResponseDto> getPosts(int page, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<NormalPost> normalPosts = normalPostRepository.findAll(pageRequest).getContent();
        return NormalPostMapper.toPostListResponseDto(normalPosts, likeService.getLikeCountMap(normalPosts), member);
    }

    @Override
    public List<PostResponseDto> getPostsByMember(Long memberId, int page, String email) {
        memberQueryUseCase.findOrThrow(memberId);
        Member member = memberQueryUseCase.findOrThrow(email);
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<NormalPost> normalPosts = normalPostRepository.findAllByMemberId(memberId, pageRequest).getContent();
        return NormalPostMapper.toPostListResponseDto(normalPosts, likeService.getLikeCountMap(normalPosts), member);
    }

    @Transactional
    @Override
    public GetPostResponseDto getPost(Long postId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        NormalPost normalPost = queryService.findByNormalPost(postId);
        normalPostRepository.incrementViewCount(normalPost.getId());
        return NormalPostMapper.toGetPostResponseDto(normalPost, member, likeRepository.countByPost(normalPost), likeRepository.existsByPostAndMember(normalPost, member));
    }

    @Transactional
    @Override
    public CreatePostResponseDto createPost(PostDto createPostDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        String imageFileName = FileUploadUtil.fileUpload(createPostDto.getPostImageFile(), postUploadDir, FileImageKind.POST);
        NormalPost normalPost = NormalPostMapper.toEntity(createPostDto, imageFileName, member);
        NormalPost savedPost = normalPostRepository.save(normalPost);
        return new CreatePostResponseDto(savedPost.getId());
    }

    @Transactional
    @Override
    public void deletePost(Long postId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        NormalPost normalPost = queryService.findByNormalPost(postId);
        normalPost.validateMember(member);
        normalPostRepository.deleteById(postId);
    }

    @Transactional
    @Override
    public void updatePost(Long postId, PostDto updatePostDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        NormalPost normalPost = queryService.findByNormalPost(postId);
        normalPost.validateMember(member);
        normalPost.updateNormalPost(FileUploadUtil.fileUpload(updatePostDto.getPostImageFile(), postUploadDir, FileImageKind.POST), updatePostDto.getTitle(), updatePostDto.getContent());
    }
}

