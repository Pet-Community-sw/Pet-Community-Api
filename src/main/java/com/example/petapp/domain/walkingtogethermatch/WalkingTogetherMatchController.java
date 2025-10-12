package com.example.petapp.domain.walkingtogethermatch;

import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.request.CreateWalkingTogetherMatchDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.response.CreateWalkingTogetherMatchResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.response.GetWalkingTogetherMatchResponseDto;
import com.example.petapp.domain.walkingtogethermatch.model.dto.request.UpdateWalkingTogetherMatchDto;
import com.example.petapp.common.base.util.AuthUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/walking-together-posts")
public class WalkingTogetherMatchController {

    private final WalkingTogetherMatchService walkingTogetherMatchService;


    @GetMapping("/{walkingTogetherPostId}")
    private GetWalkingTogetherMatchResponseDto getWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        return walkingTogetherMatchService.getWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
    }

    @GetMapping("/by-recommend-route-post/{recommendRoutePostId}")
    private List<GetWalkingTogetherMatchResponseDto> getWalkingTogetherPosts(@PathVariable Long recommendRoutePostId, Authentication authentication) {
        return walkingTogetherMatchService.getWalkingTogetherPosts(recommendRoutePostId, AuthUtil.getProfileId(authentication));
    }

    @PostMapping
    private CreateWalkingTogetherMatchResponseDto createWalkingTogetherPost(@RequestBody @Valid CreateWalkingTogetherMatchDto createWalkingTogetherMatchDto, Authentication authentication) {
        return walkingTogetherMatchService.createWalkingTogetherPost(createWalkingTogetherMatchDto, AuthUtil.getProfileId(authentication));
    }

    @PutMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> updateWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, @RequestBody @Valid UpdateWalkingTogetherMatchDto updateWalkingTogetherMatchDto, Authentication authentication) {
        walkingTogetherMatchService.updateWalkingTogetherPost(walkingTogetherPostId,
                updateWalkingTogetherMatchDto,
                AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> deleteWalkingTogetherPost(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        walkingTogetherMatchService.deleteWalkingTogetherPost(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @PostMapping("/{walkingTogetherPostId}")
    private ResponseEntity<MessageResponse> startMatch(@PathVariable Long walkingTogetherPostId, Authentication authentication) {
        CreateChatRoomResponseDto createChatRoomResponseDto =
                walkingTogetherMatchService.startMatch(walkingTogetherPostId, AuthUtil.getProfileId(authentication));
        if (createChatRoomResponseDto.isCreated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(createChatRoomResponseDto.getChatRoomId().toString()));
        } else {
            return ResponseEntity.ok(new MessageResponse("매칭 완료."));
        }
    }
}
