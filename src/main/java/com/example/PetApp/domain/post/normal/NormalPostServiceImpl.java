package com.example.PetApp.domain.post.normal;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.post.normal.model.entity.NormalPost;
import com.example.PetApp.domain.post.normal.model.dto.response.CreatePostResponseDto;
import com.example.PetApp.domain.post.normal.model.dto.request.PostDto;
import com.example.PetApp.domain.post.normal.model.dto.response.GetPostResponseDto;
import com.example.PetApp.domain.post.normal.model.dto.response.PostResponseDto;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.post.normal.mapper.NormalPostMapper;
import com.example.PetApp.domain.like.LikeRepository;
import com.example.PetApp.domain.like.LikeService;
import com.example.PetApp.domain.query.QueryService;
import com.example.PetApp.common.base.util.imagefile.FileUploadUtil;
import com.example.PetApp.common.base.util.imagefile.FileImageKind;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class NormalPostServiceImpl implements NormalPostService {

    @Value("${spring.dog.post.image.upload}")
    private String postUploadDir;

    private final NormalPostRepository normalPostRepository;
    private final LikeRepository likeRepository;
    private final LikeService likeService;
    private final QueryService queryService;
    private final RedisTemplate<String, Long> likeRedisTemplate;

    @Transactional(readOnly = true)
    @Override
    public List<PostResponseDto> getPosts(int page, String email) {
        Member member = queryService.findByMember(email);
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<NormalPost> normalPosts = normalPostRepository.findAll(pageRequest).getContent();
        Set<Long> members = likeRedisTemplate.opsForSet().members("member:likes:" + member.getId());
        return NormalPostMapper.toPostListResponseDto(normalPosts, likeService.getLikeCountMap(normalPosts), members);
    }

    @Transactional
    @Override
    public GetPostResponseDto getPost(Long postId, String email) {
        Member member = queryService.findByMember(email);
        NormalPost normalPost = queryService.findByNormalPost(postId);
        normalPost.updateViewCount(member);

        return NormalPostMapper.toGetPostResponseDto(normalPost, member, likeRepository.countByPost(normalPost), likeRepository.existsByPostAndMember(normalPost, member));
    }

    @Transactional
    @Override
    public CreatePostResponseDto createPost(PostDto createPostDto, String email) {
        Member member = queryService.findByMember(email);
        String imageFileName = FileUploadUtil.fileUpload(createPostDto.getPostImageFile(), postUploadDir, FileImageKind.POST);
        NormalPost normalPost = NormalPostMapper.toEntity(createPostDto, imageFileName, member);
        NormalPost savedPost = normalPostRepository.save(normalPost);
        return new CreatePostResponseDto(savedPost.getId());
    }

    @Transactional
    @Override
    public void deletePost(Long postId, String email) {
        Member member = queryService.findByMember(email);
        NormalPost normalPost = queryService.findByNormalPost(postId);
        validateMember(normalPost, member);
        normalPostRepository.deleteById(postId);
    }

    @Transactional
    @Override
    public void updatePost(Long postId, PostDto updatePostDto, String email) {
        Member member = queryService.findByMember(email);
        NormalPost normalPost = queryService.findByNormalPost(postId);
        validateMember(normalPost, member);

        normalPost.updateNormalPost(FileUploadUtil.fileUpload(updatePostDto.getPostImageFile(), postUploadDir, FileImageKind.POST), updatePostDto.getTitle(), updatePostDto.getContent());
    }

    private static void validateMember(NormalPost normalPost, Member member) {
        if (!(normalPost.getMember().equals(member))) {
            throw new ForbiddenException("권한 없음.");
        }
    }

}

