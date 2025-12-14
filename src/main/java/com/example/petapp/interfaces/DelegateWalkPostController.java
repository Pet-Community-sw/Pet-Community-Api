package com.example.petapp.interfaces;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.post.delegate.DelegateWalkPostUseCase;
import com.example.petapp.application.in.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.petapp.application.in.post.delegate.model.dto.request.GetDelegatePostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.domain.post.model.Applicant;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Tag(name = "DelegateWalkPost")
@RestController
@RequiredArgsConstructor
@RequestMapping("/delegate-walk-posts")
public class DelegateWalkPostController {

    private final DelegateWalkPostUseCase delegateWalkPostUseCase;

    @Operation(
            summary = "대리 산책 게시글 생성"
    )
    @PostMapping
    public CreateDelegateWalkPostResponseDto createDelegateWalkPost(@RequestBody @Valid CreateDelegateWalkPostDto createDelegateWalkPostDto, Authentication authentication) {
        return delegateWalkPostUseCase.createDelegateWalkPost(createDelegateWalkPostDto, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "대리 산책 게시글에 지원"
    )
    @PostMapping("/{delegateWalkPostId}")
    public ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(@PathVariable Long delegateWalkPostId,
                                                                      @RequestBody String content,
                                                                      Authentication authentication) {
        return delegateWalkPostUseCase.applyToDelegateWalkPost(delegateWalkPostId, content, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "대리 산책 게시글 목록 조회(위치 범위)"
    )
    @GetMapping("/by-location")
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(@RequestParam Double minLongitude,
                                                                                @RequestParam Double minLatitude,
                                                                                @RequestParam Double maxLongitude,
                                                                                @RequestParam Double maxLatitude,
                                                                                @RequestParam(defaultValue = "1", required = false) int page,
                                                                                Authentication authentication) {
        return delegateWalkPostUseCase.getDelegateWalkPostsByLocation(minLongitude, minLatitude, maxLongitude, maxLatitude, page, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "대리 산책 게시글 목록 조회(위치 반경 내)"
    )
    @GetMapping("/by-place")
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(@RequestParam Double longitude,
                                                                             @RequestParam Double latitude,
                                                                             @RequestParam(defaultValue = "1", required = false) int page,
                                                                             Authentication authentication) {
        return delegateWalkPostUseCase.getDelegateWalkPostsByPlace(longitude, latitude, page, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "대리 산책 게시글 상세 조회"
    )
    @GetMapping("/{delegateWalkPostId}")
    public GetDelegatePostResponseDto getDelegateWalkPost(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        return delegateWalkPostUseCase.getDelegateWalkPost(delegateWalkPostId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "대리 산책 게시글 지원자 조회"
    )
    @GetMapping("/applicants/{delegateWalkPostId}")
    public Set<Applicant> getApplicants(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        return delegateWalkPostUseCase.getApplicants(delegateWalkPostId, AuthUtil.getProfileId(authentication));
    }


    @Operation(
            summary = "대리 산책자 산책 시작권한 부여"
    )
    @PutMapping("/{delegateWalkPostId}/start-authorized")//산책 시작권한을 줌.
    public CreateWalkRecordResponseDto grantAuthorize(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        return delegateWalkPostUseCase.grantAuthorize(delegateWalkPostId, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "대리 산책 게시글 수정"
    )
    @PutMapping("/{delegateWalkPostId}")
    public ResponseEntity<MessageResponse> updateDelegateWalkPost(@PathVariable Long delegateWalkPostId, @RequestBody UpdateDelegateWalkPostDto updateDelegateWalkPostDto, Authentication authentication) {
        delegateWalkPostUseCase.updateDelegateWalkPost(delegateWalkPostId, updateDelegateWalkPostDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "대리 산책 게시글 삭제"
    )
    @DeleteMapping("/{delegateWalkPostId}")
    public ResponseEntity<MessageResponse> deleteDelegateWalkPost(@PathVariable Long delegateWalkPostId, Authentication authentication) {
        delegateWalkPostUseCase.deleteDelegateWalkPost(delegateWalkPostId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @Operation(
            summary = "대리 산책자 지원자 선정"
    )
    @PostMapping("/{delegateWalkPostId}/select-applicant")
    public CreateChatRoomResponseDto selectApplicant(@PathVariable Long delegateWalkPostId, @RequestBody Long memberId, Authentication authentication) {
        return delegateWalkPostUseCase.selectApplicant(delegateWalkPostId, memberId, AuthUtil.getEmail(authentication));
    }
}
