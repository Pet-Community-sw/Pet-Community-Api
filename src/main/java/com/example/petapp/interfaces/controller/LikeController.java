package com.example.petapp.interfaces.controller;


import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.like.LikeQueryUseCase;
import com.example.petapp.application.in.like.LikeUseCase;
import com.example.petapp.application.in.like.dto.response.LikeResponseDto;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * todo:좋아요를 따로 둬야할지? 고민
 */
@Tag(name = "Likes")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeUseCase likeUseCase;
    private final LikeQueryUseCase likeQueryUseCase;

    @Operation(
            summary = "좋아요 목록 조회"
    )
    @GetMapping("/{postId}")//api 명세서 수정해야함.
    public LikeResponseDto getLikes(@PathVariable Long postId) {
        return likeQueryUseCase.get(postId);
    }

    @Operation(
            summary = "좋아요 생성 및 삭제"
    )
    @PostMapping()
    public ResponseEntity<MessageResponse> createAndDeleteLike(@RequestBody Long postId, Authentication authentication) {
        return likeUseCase.createAndDelete(postId, AuthUtil.getMemberId(authentication)) ?
                ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("좋아요 생성했습니다.")) :
                ResponseEntity.ok(new MessageResponse("좋아요 삭제했습니다."));
    }

}
