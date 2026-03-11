package com.example.petapp.interfaces.controller;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.post.recommend.RecommendRoutePostUseCase;
import com.example.petapp.application.in.post.recommend.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.request.UpdateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.response.CreateRecommendRoutePostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendRoutePostsResponseDto;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "RecommendRoutePost")
@RestController
@RequestMapping("/recommend-route-posts")
@RequiredArgsConstructor
public class RecommendRoutePostController {

    private final RecommendRoutePostUseCase recommendRoutePostUseCase;

    @Operation(
            summary = "산책길 추천 게시글 생성"
    )
    @PostMapping
    private CreateRecommendRoutePostResponseDto createRecommendRoutePost(@RequestBody @Valid CreateRecommendRoutePostDto createRecommendRoutePostDto,
                                                                         Authentication authentication) {
        return recommendRoutePostUseCase.createRecommendRoutePost(createRecommendRoutePostDto, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 목록 조회(위치 범위)"
    )
    @GetMapping("/by-location")
    private List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(@RequestParam Double minLongitude,
                                                                           @RequestParam Double minLatitude,
                                                                           @RequestParam Double maxLongitude,
                                                                           @RequestParam Double maxLatitude,
                                                                           @RequestParam(required = false, defaultValue = "1") int page,
                                                                           Authentication authentication) {
        return recommendRoutePostUseCase.getRecommendRoutePosts(minLongitude, minLatitude, maxLongitude, maxLatitude, page, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 목록 조회(위치 반경 내)"
    )
    @GetMapping("/by-place")
    private List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(@RequestParam Double longitude,
                                                                           @RequestParam Double latitude,
                                                                           @RequestParam(required = false, defaultValue = "1") int page,
                                                                           Authentication authentication) {
        return recommendRoutePostUseCase.getRecommendRoutePosts(longitude, latitude, page, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 상세 조회"
    )
    @GetMapping("/{recommendRoutePostId}")
    private GetRecommendPostResponseDto getRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        return recommendRoutePostUseCase.getRecommendRoutePost(recommendRoutePostId, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 수정"
    )
    @PutMapping("/{recommendRoutePostId}")
    private ResponseEntity<MessageResponse> updateRecommendRoutePost(@PathVariable Long recommendRoutePostId,
                                                                     @RequestBody @Valid UpdateRecommendRoutePostDto updateRecommendRoutePostDto,
                                                                     Authentication authentication) {
        recommendRoutePostUseCase.updateRecommendRoutePost(recommendRoutePostId, updateRecommendRoutePostDto, AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "산책길 추천 게시글 삭제"
    )
    @DeleteMapping("/{recommendRoutePostId}")
    private ResponseEntity<MessageResponse> deleteRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        recommendRoutePostUseCase.deleteRecommendRoutePost(recommendRoutePostId, AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }
}
