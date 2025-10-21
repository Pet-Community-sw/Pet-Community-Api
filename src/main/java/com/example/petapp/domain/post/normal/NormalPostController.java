package com.example.petapp.domain.post.normal;

import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import com.example.petapp.domain.post.normal.model.dto.request.PostDto;
import com.example.petapp.domain.post.normal.model.dto.response.CreatePostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.response.GetPostResponseDto;
import com.example.petapp.domain.post.normal.model.dto.response.PostResponseDto;
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

    private final NormalPostService normalPostService;

    @Operation(
            summary = "게시물 목록 조회"
    )
    @GetMapping()
    public List<PostResponseDto> getPosts(@RequestParam(defaultValue = "1") int page, Authentication authentication) {
        return normalPostService.getPosts(page, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "게시물 생성"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreatePostResponseDto createPost(@ModelAttribute @Validated PostDto createPostDto, Authentication authentication) {
        return normalPostService.createPost(createPostDto, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "게시물 상세 조회"
    )
    @GetMapping("/{postId}")//요청시 댓글까지 한번에 반환. 상세게시물을 보면 무조건 댓글까지 보이게 할거임 그리고 댓글수가 많지않은 커뮤니티라 판단함.
    public GetPostResponseDto getPost(@PathVariable Long postId, Authentication authentication) {
        return normalPostService.getPost(postId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "게시물 수정"
    )
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updatePost(@PathVariable Long postId, @ModelAttribute @Validated PostDto postDto, Authentication authentication) {
        normalPostService.updatePost(postId, postDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "게시물 삭제"
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId, Authentication authentication) {
        normalPostService.deletePost(postId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }
}
