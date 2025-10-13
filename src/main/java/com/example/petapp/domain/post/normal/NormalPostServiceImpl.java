package com.example.petapp.domain.post.normal;

import com.example.petapp.common.base.util.imagefile.FileImageKind;
import com.example.petapp.common.base.util.imagefile.FileUploadUtil;
import com.example.petapp.common.exception.ForbiddenException;
import com.example.petapp.domain.like.LikeRepository;
import com.example.petapp.domain.like.LikeService;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.normal.mapper.NormalPostMapper;
import com.example.petapp.domain.post.normal.model.dto.request.PostDto;
import com.example.petapp.domain.post.normal.model.dto.response.CreatePostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.response.GetPostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.response.PostResponseDto;
import com.example.petapp.domain.post.normal.model.entity.NormalPost;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class NormalPostServiceImpl implements NormalPostService {

    @Value("${spring.dog.post.image.upload}")
    private String postUploadDir;

    private final NormalPostRepository normalPostRepository;
    private final LikeRepository likeRepository;
    private final LikeService likeService;
    private final QueryService queryService;
    private final InMemoryService inMemoryService;

    @Transactional(readOnly = true)
    @Override
    public List<PostResponseDto> getPosts(int page, String email) {
        Member member = queryService.findByMember(email);
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        List<NormalPost> normalPosts = normalPostRepository.findAll(pageRequest).getContent();
        Set<Long> memberIds = inMemoryService.getLikeData(member.getId());
        return NormalPostMapper.toPostListResponseDto(normalPosts, likeService.getLikeCountMap(normalPosts), memberIds);
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

