package com.example.petapp.domain.post.recommend;

import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import com.example.petapp.domain.post.recommend.model.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.domain.post.recommend.model.dto.request.UpdateRecommendRoutePostDto;
import com.example.petapp.domain.post.recommend.model.dto.response.CreateRecommendRoutePostResponseDto;
import com.example.petapp.domain.post.recommend.model.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.domain.post.recommend.model.dto.response.GetRecommendRoutePostsResponseDto;
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

    private final RecommendRoutePostService recommendRoutePostService;

    @Operation(
            summary = "산책길 추천 게시글 생성"
    )
    @PostMapping
    private CreateRecommendRoutePostResponseDto createRecommendRoutePost(@RequestBody @Valid CreateRecommendRoutePostDto createRecommendRoutePostDto,
                                                                         Authentication authentication) {
        return recommendRoutePostService.createRecommendRoutePost(createRecommendRoutePostDto, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 목록 조회(위치 범위)"
    )
    @GetMapping("/by-location")
    private List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(@RequestParam Double minLongitude,
                                                                           @RequestParam Double minLatitude,
                                                                           @RequestParam Double maxLongitude,
                                                                           @RequestParam Double maxLatitude,
                                                                           @RequestParam int page,
                                                                           Authentication authentication) {
        return recommendRoutePostService.getRecommendRoutePosts(minLongitude, minLatitude, maxLongitude, maxLatitude, page, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 목록 조회(위치 반경 내)"
    )
    @GetMapping("/by-place")
    private List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(@RequestParam Double longitude,
                                                                           @RequestParam Double latitude,
                                                                           @RequestParam int page,
                                                                           Authentication authentication) {
        return recommendRoutePostService.getRecommendRoutePosts(longitude, latitude, page, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 상세 조회"
    )
    @GetMapping("/{recommendRoutePostId}")
    private GetRecommendPostResponseDto getRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        return recommendRoutePostService.getRecommendRoutePost(recommendRoutePostId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "산책길 추천 게시글 수정"
    )
    @PutMapping("/{recommendRoutePostId}")
    private ResponseEntity<MessageResponse> updateRecommendRoutePost(@PathVariable Long recommendRoutePostId,
                                                                     @RequestBody @Valid UpdateRecommendRoutePostDto updateRecommendRoutePostDto,
                                                                     Authentication authentication) {
        recommendRoutePostService.updateRecommendRoutePost(recommendRoutePostId, updateRecommendRoutePostDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "산책길 추천 게시글 삭제"
    )
    @DeleteMapping("/{recommendRoutePostId}")
    private ResponseEntity<MessageResponse> deleteRecommendRoutePost(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        recommendRoutePostService.deleteRecommendRoutePost(recommendRoutePostId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }
}
