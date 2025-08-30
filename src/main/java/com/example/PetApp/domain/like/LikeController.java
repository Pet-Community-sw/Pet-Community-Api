package com.example.PetApp.domain.like;


import com.example.PetApp.domain.like.model.dto.response.LikeResponseDto;
import com.example.PetApp.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{postId}")//api 명세서 수정해야함.
    public LikeResponseDto getLikes(@PathVariable Long postId) {
        return likeService.getLikes(postId);
    }

    @PostMapping()
    public ResponseEntity<?> createAndDeleteLike(@RequestBody Long postId, Authentication authentication) {
        return likeService.createAndDeleteLike(postId, AuthUtil.getEmail(authentication));
    }
}
