package com.example.PetApp.domain.post.delegate;

import com.example.PetApp.domain.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.GetPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.PetApp.infrastructure.database.base.embedded.Applicant;
import com.example.PetApp.infrastructure.app.common.MessageResponse;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.CreateMemberChatRoomResponseDto;
import com.example.PetApp.domain.walkrecord.model.dto.response.CreateWalkRecordResponseDto;
import com.example.PetApp.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delegate-walk-posts")
public class DelegateWalkPostController {

    private final DelegateWalkPostService delegateWalkPostService;

    @PostMapping
    public CreateDelegateWalkPostResponseDto createDelegateWalkPost(@RequestBody @Valid CreateDelegateWalkPostDto createDelegateWalkPostDto, Authentication authentication) {
        return delegateWalkPostService.createDelegateWalkPost(createDelegateWalkPostDto, AuthUtil.getProfileId(authentication));
    }

    @PostMapping("/{delegateWalkPostId}")
    public ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(@PathVariable Long delegateWalkPostId,
                                                                      @RequestBody String content,
                                                                      Authentication authentication) {
        return delegateWalkPostService.applyToDelegateWalkPost(delegateWalkPostId, content, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/by-location")
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(@RequestParam Double minLongitude,
                                                                                @RequestParam Double minLatitude,
                                                                                @RequestParam Double maxLongitude,
                                                                                @RequestParam Double maxLatitude,
                                                                                Authentication authentication) {
        return delegateWalkPostService.getDelegateWalkPostsByLocation(minLongitude, minLatitude, maxLongitude, maxLatitude,AuthUtil.getEmail(authentication));
    }

    @GetMapping("/by-place")
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(@RequestParam Double longitude,
                                                                             @RequestParam Double latitude,
                                                                             Authentication authentication) {
        return delegateWalkPostService.getDelegateWalkPostsByPlace(longitude, latitude, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{delegateWalkPostId}")
    public GetPostResponseDto getDelegateWalkPost(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        return delegateWalkPostService.getDelegateWalkPost(delegateWalkPostId, AuthUtil.getEmail(authentication));
    }

    @GetMapping("/applicants/{delegateWalkPostId}")
    public Set<Applicant> getApplicants(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        return delegateWalkPostService.getApplicants(delegateWalkPostId, AuthUtil.getProfileId(authentication));
    }


    @PutMapping("/start-authorized/{delegateWalkPostId}")//산책 시작권한을 줌.
    public CreateWalkRecordResponseDto grantAuthorize(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        return delegateWalkPostService.grantAuthorize(delegateWalkPostId, AuthUtil.getProfileId(authentication));
    }

    @PutMapping("/{delegateWalkPostId}")
    public ResponseEntity<MessageResponse> updateDelegateWalkPost(@PathVariable Long delegateWalkPostId, @RequestBody UpdateDelegateWalkPostDto updateDelegateWalkPostDto, Authentication authentication) {
        delegateWalkPostService.updateDelegateWalkPost(delegateWalkPostId, updateDelegateWalkPostDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{delegateWalkPostId}")
    public ResponseEntity<MessageResponse> deleteDelegateWalkPost(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        delegateWalkPostService.deleteDelegateWalkPost(delegateWalkPostId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @PostMapping("/select-applicant/{delegateWalkPostId}")
    public CreateMemberChatRoomResponseDto selectApplicant(@PathVariable Long delegateWalkPostId, @RequestBody Long memberId, Authentication authentication) {
        return delegateWalkPostService.selectApplicant(delegateWalkPostId, memberId, AuthUtil.getEmail(authentication));
    }

}
