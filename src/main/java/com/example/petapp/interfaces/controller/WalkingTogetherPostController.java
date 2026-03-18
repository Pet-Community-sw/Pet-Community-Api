package com.example.petapp.interfaces.controller;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.match.WalkingTogetherPostUseCase;
import com.example.petapp.application.in.match.dto.request.CreateWalkingTogetherPostDto;
import com.example.petapp.application.in.match.dto.request.UpdateWalkingTogetherPostDto;
import com.example.petapp.application.in.match.dto.response.CreateWalkingTogetherPostResponseDto;
import com.example.petapp.application.in.match.dto.response.GetWalkingTogetherPostResponseDto;
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

@Tag(name = "WalkingTogetherPosts")
@RestController
@RequiredArgsConstructor
@RequestMapping("/walking-together-posts")
public class WalkingTogetherPostController {

    private final WalkingTogetherPostUseCase walkingTogetherPostUseCase;

    @Operation(
            summary = "함께 산책해요 게시글 상세 조회"
    )
    @GetMapping("/{walkingTogetherPostId}")
    private GetWalkingTogetherPostResponseDto getWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        return walkingTogetherPostUseCase.getWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "함께 산책해요 게시글 목록 조회"
    )
    @GetMapping("/{recommendRoutePostId}")
    private List<GetWalkingTogetherPostResponseDto> getWalkingTogetherPosts(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        return walkingTogetherPostUseCase.getWalkingTogetherPosts(recommendRoutePostId, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "함께 산책해요 게시글 작성"
    )
    @PostMapping
    private CreateWalkingTogetherPostResponseDto createWalkingTogetherPost(@RequestBody @Valid CreateWalkingTogetherPostDto createWalkingTogetherPostDto, Authentication authentication) {
        return walkingTogetherPostUseCase.createWalkingTogetherPost(createWalkingTogetherPostDto, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "함께 산책해요 게시글 수정"
    )
    @PutMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> updateWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, @RequestBody @Valid UpdateWalkingTogetherPostDto updateWalkingTogetherPostDto, Authentication authentication) {
        walkingTogetherPostUseCase.updateWalkingTogetherPost(walkingTogetherPostId, updateWalkingTogetherPostDto, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "함께 산책해요 게시글 삭제"
    )
    @DeleteMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> deleteWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        walkingTogetherPostUseCase.deleteWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @Operation(
            summary = "함께 산책해요 매칭 시작"
    )
    @PostMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> startMatch(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        CreateChatRoomResponseDto createChatRoomResponseDto = walkingTogetherPostUseCase.startMatch(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        return createChatRoomResponseDto.isCreated() ?
                ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(createChatRoomResponseDto.getChatRoomId().toString())) :
                ResponseEntity.ok(new MessageResponse("매칭 완료."));
    }
}
