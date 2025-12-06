package com.example.petapp.interfaces;

import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.in.profile.dto.request.ProfileDto;
import com.example.petapp.application.in.profile.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.petapp.application.in.profile.dto.response.CreateProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.GetProfileResponseDto;
import com.example.petapp.application.in.profile.dto.response.ProfileListResponseDto;
import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Profile")
@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileUseCase profileUseCase;

    @Operation(
            summary = "자신이 생성한 프로필 목록 조회"
    )
    @GetMapping
    public List<ProfileListResponseDto> getProfiles(Authentication authentication) {//dogBreed를 안내보내도 될듯? 효빈이랑 얘기해봐야됨.
        return profileUseCase.getProfiles(AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "프로필 상세 조회"
    )
    @GetMapping("/{profileId}")
    public GetProfileResponseDto getProfile(@PathVariable Long profileId, Authentication authentication) {
        return profileUseCase.getProfile(profileId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "프로필 생성"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateProfileResponseDto createProfile(@ModelAttribute @Validated ProfileDto profileDto, Authentication authentication) {
        return profileUseCase.createProfile(profileDto, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "프로필 수정"
    )
    @PutMapping(value = "/{profileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updateProfile(@PathVariable Long profileId, @ModelAttribute @Validated ProfileDto addProfileDto, Authentication authentication) {
        profileUseCase.updateProfile(profileId, addProfileDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "프로필 삭제"
    )
    @DeleteMapping("/{profileId}")//삭제 수정도 authentication에 profileId가 추가되어있어야함.
    public ResponseEntity<MessageResponse> deleteProfile(@PathVariable Long profileId, Authentication authentication) {
        profileUseCase.deleteProfile(profileId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @Operation(
            summary = "프로필 전용 토큰"
    )
    @PostMapping("/token/{profileId}")//리팩토링 시에 authentication 말고 accesstoken을 받아서 이전 토큰 무효화 처리해야됨.
    public AccessTokenByProfileIdResponseDto accessTokenByProfileId(@RequestHeader("Authorization") String accessToken, @PathVariable Long profileId, Authentication authentication) {
        return profileUseCase.accessTokenByProfile(accessToken, profileId, AuthUtil.getEmail(authentication));
    }

}
