package com.example.petapp.interfaces.controller;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.match.WalkingTogetherMatchUseCase;
import com.example.petapp.application.in.match.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.application.in.match.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.petapp.application.in.match.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.petapp.application.in.match.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "WalkingTogetherPost")
@RestController
@RequiredArgsConstructor
@RequestMapping("/walking-together-posts")
public class WalkingTogetherMatchController {

    private final WalkingTogetherMatchUseCase walkingTogetherMatchUseCase;

    @Operation(
            summary = "함께 산책해요 게시글 상세 조회"
    )
    @GetMapping("/{walkingTogetherPostId}")
    private GetWalkingTogetherMatchResponseDto getWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        return walkingTogetherMatchUseCase.getWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "함께 산책해요 게시글 목록 조회"
    )
    @GetMapping("/by-recommend-route-post/{recommendRoutePostId}")
    private List<GetWalkingTogetherMatchResponseDto> getWalkingTogetherPosts(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        return walkingTogetherMatchUseCase.getWalkingTogetherPosts(recommendRoutePostId, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "함께 산책해요 게시글 작성"
    )
    @PostMapping
    private CreateWalkingTogetherMatchResponseDto createWalkingTogetherPost(@RequestBody @Valid CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto, Authentication authentication) {
        return walkingTogetherMatchUseCase.createWalkingTogetherPost(createWalkingTogetherMatchDto, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "함께 산책해요 게시글 수정"
    )
    @PutMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> updateWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, @RequestBody @Valid UpdateWalkingTogetherMatchDto updateWalkingTogetherMatchDto, Authentication authentication) {
        walkingTogetherMatchUseCase.updateWalkingTogetherPost(walkingTogetherPostId, updateWalkingTogetherMatchDto, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "함께 산책해요 게시글 삭제"
    )
    @DeleteMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> deleteWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        walkingTogetherMatchUseCase.deleteWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @Operation(
            summary = "함께 산책해요 매칭 시작"
    )
    @PostMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> startMatch(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        CreateChatRoomResponseDto createChatRoomResponseDto = walkingTogetherMatchUseCase.startMatch(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        return createChatRoomResponseDto.isCreated() ?
                ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(createChatRoomResponseDto.getChatRoomId().toString())) :
                ResponseEntity.ok(new MessageResponse("매칭 완료."));
    }
}
