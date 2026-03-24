package com.example.petapp.interfaces.controller;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.member.object.dto.request.MemberSignDto;
import com.example.petapp.application.in.member.object.dto.request.ResetPasswordDto;
import com.example.petapp.application.in.member.object.dto.request.UpdateMemberRequestDto;
import com.example.petapp.application.in.member.object.dto.response.FindByIdResponseDto;
import com.example.petapp.application.in.member.object.dto.response.GetMemberResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSearchResponseDto;
import com.example.petapp.application.in.member.object.dto.response.MemberSignResponseDto;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Members")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @Operation(
            summary = "회원가입",
            description = "프로필 이미지는 선택, 기본이미지 하고싶으면 send empty value 체크하지말고 그냥 요청"
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MemberSignResponseDto> createMember(@ModelAttribute @Valid MemberSignDto memberSignDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberUseCase.create(memberSignDto));
    }


    @Operation(
            summary = "유저 상세 조회"
    )
    @GetMapping("/{memberId}")
    public GetMemberResponseDto getMember(@PathVariable Long memberId, Authentication authentication) {
        return memberUseCase.get(memberId, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "아이디 찾기"
    )
    @GetMapping
    public FindByIdResponseDto findById(@RequestParam String phoneNumber) {
        return memberUseCase.findById(phoneNumber);
    }

    @Operation(
            summary = "비밀번호 재설정"
    )
    @PutMapping("/password")//수정 필요 토큰 있을 때와 없을 때
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto, Authentication authentication) {
        memberUseCase.resetPassword(resetPasswordDto, AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 변경되었습나다."));
    }

    @Operation(
            summary = "회원 수정"
    )
    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponse> updateMember(@ModelAttribute @Valid UpdateMemberRequestDto requestDto, Authentication authentication) {
        memberUseCase.update(requestDto, AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 했습니다."));
    }

    @Operation(
            summary = "회원탈퇴"
    )
    @DeleteMapping()
    public ResponseEntity<MessageResponse> deleteMember(Authentication authentication) {
        memberUseCase.delete(AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("탈퇴 되었습니다."));
    }

    @Operation(
            summary = "검색 추천"
    )
    @GetMapping("/search-suggestions")
    public List<MemberSearchResponseDto> searchSuggestions(@RequestParam String keyword, Authentication authentication) {
        return memberUseCase.searchSuggestions(keyword, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "검색"
    )
    @GetMapping("/searches")
    public List<MemberSearchResponseDto> searchMembers(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, Authentication authentication) {
        return memberUseCase.searchMembers(keyword, page, AuthUtil.getMemberId(authentication));
    }

//    @Operation(
//            summary = "fcm토큰 생성"
//    )
//    @PostMapping("/fcm-token")
//    public ResponseEntity<MessageResponse> createFcmToken(@RequestBody @Valid FcmTokenDto fcmTokenDto) {
//        memberUseCase.createFcmToken(fcmTokenDto);
//        return ResponseEntity.ok(new MessageResponse("fcm토큰 생성완료."));
//    }
}
