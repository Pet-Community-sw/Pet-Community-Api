package com.example.PetApp.domain.post.normal;

import com.example.PetApp.common.app.common.MessageResponse;
import com.example.PetApp.domain.post.normal.model.dto.response.CreatePostResponseDto;
import com.example.PetApp.domain.post.normal.model.dto.response.GetPostResponseDto;
import com.example.PetApp.domain.post.normal.model.dto.request.PostDto;
import com.example.PetApp.domain.post.normal.model.dto.response.PostResponseDto;
import com.example.PetApp.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class NormalPostController {

    private final NormalPostService normalPostService;

    @GetMapping()
    public List<PostResponseDto> getPosts(@RequestParam(defaultValue = "0") int page, Authentication authentication) {
        return normalPostService.getPosts(page, AuthUtil.getEmail(authentication));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreatePostResponseDto createPost(@ModelAttribute @Validated PostDto createPostDto, Authentication authentication) {
        return normalPostService.createPost(createPostDto, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{postId}")//요청시 댓글까지 한번에 반환. 상세게시물을 보면 무조건 댓글까지 보이게 할거임 그리고 댓글수가 많지않은 커뮤니티라 판단함.
    public GetPostResponseDto getPost(@PathVariable Long postId, Authentication authentication) {
        return normalPostService.getPost(postId, AuthUtil.getEmail(authentication));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable Long postId, Authentication authentication) {
        normalPostService.deletePost(postId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updatePost(@PathVariable Long postId, @ModelAttribute @Validated PostDto postDto, Authentication authentication) {
        normalPostService.updatePost(postId, postDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }
}
