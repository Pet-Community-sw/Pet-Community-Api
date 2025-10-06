package com.example.PetApp.domain.profile;

import com.example.PetApp.domain.profile.model.dto.request.ProfileDto;
import com.example.PetApp.domain.profile.model.dto.response.AccessTokenByProfileIdResponseDto;
import com.example.PetApp.domain.profile.model.dto.response.CreateProfileResponseDto;
import com.example.PetApp.domain.profile.model.dto.response.GetProfileResponseDto;
import com.example.PetApp.domain.profile.model.dto.response.ProfileListResponseDto;
import com.example.PetApp.common.app.common.MessageResponse;
import com.example.PetApp.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public List<ProfileListResponseDto> getProfiles(Authentication authentication) {//dogBreed를 안내보내도 될듯? 효빈이랑 얘기해봐야됨.
        return profileService.getProfiles(AuthUtil.getEmail(authentication));
    }

    @GetMapping("/{profileId}")
    public GetProfileResponseDto getProfile(@PathVariable Long profileId, Authentication authentication) {
        return profileService.getProfile(profileId, AuthUtil.getEmail(authentication));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateProfileResponseDto createProfile(@ModelAttribute @Validated ProfileDto profileDto, Authentication authentication) {
        return profileService.createProfile(profileDto, AuthUtil.getEmail(authentication));
    }

    @PutMapping(value = "/{profileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> updateProfile(@PathVariable Long profileId, @ModelAttribute @Validated ProfileDto addProfileDto, Authentication authentication) {
        profileService.updateProfile(profileId, addProfileDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{profileId}")//삭제 수정도 authentication에 profileId가 추가되어있어야함.
    public ResponseEntity<MessageResponse> deleteProfile(@PathVariable Long profileId, Authentication authentication) {
        profileService.deleteProfile(profileId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @PostMapping("/token/{profileId}")//리팩토링 시에 authentication 말고 accesstoken을 받아서 이전 토큰 무효화 처리해야됨.
    public AccessTokenByProfileIdResponseDto accessTokenByProfileId(@RequestHeader("Authorization") String accessToken, @CookieValue("refreshToken") String refreshToken, @PathVariable Long profileId, Authentication authentication) {
        return profileService.accessTokenByProfile(accessToken,refreshToken, profileId, AuthUtil.getEmail(authentication));
    }

}
