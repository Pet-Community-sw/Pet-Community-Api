package com.example.petapp.interfaces;

import com.example.petapp.application.in.post.normal.NormalPostUseCase;
import com.example.petapp.application.in.post.normal.dto.request.PostDto;
import com.example.petapp.application.in.post.normal.dto.response.CreatePostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.GetPostResponseDto;
import com.example.petapp.application.in.post.normal.dto.response.PostResponseDto;
import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "NormalPost")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class NormalPostController {

    private final NormalPostUseCase normalPostUseCase;

    @Operation(
            summary = "게시물 목록 조회"
    )
    @GetMapping()
    public List<PostResponseDto> getPosts(@RequestParam(defaultValue = "1") int page, Authentication authentication) {
        return normalPostUseCase.getPosts(page, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "게시글 목록 조회 by-member"
    )
    @GetMapping("/{memberId}/by-member")
    public List<PostResponseDto> getPostsByMember(@PathVariable Long memberId, @RequestParam(defaultValue = "1") int page, Authentication authentication) {
        return normalPostUseCase.getPostsByMember(memberId, page, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "게시물 생성"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreatePostResponseDto createPost(@ModelAttribute @Validated PostDto createPostDto, Authentication authentication) {
        return normalPostUseCase.createPost(createPostDto, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "게시물 상세 조회"
    )
    @GetMapping("/{postId}")//요청시 댓글까지 한번에 반환. 상세게시물을 보면 무조건 댓글까지 보이게 할거임 그리고 댓글수가 많지않은 커뮤니티라 판단함.
    public GetPostResponseDto getPost(@PathVariable Long postId, Authentication authentication) {
        return normalPostUseCase.getPost(postId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "게시물 수정"
    )
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updatePost(@PathVariable Long postId, @ModelAttribute @Validated PostDto postDto, Authentication authentication) {
        normalPostUseCase.updatePost(postId, postDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "게시물 삭제"
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId, Authentication authentication) {
        normalPostUseCase.deletePost(postId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }
}
