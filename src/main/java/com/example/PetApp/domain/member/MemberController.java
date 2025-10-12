package com.example.PetApp.domain.member;

import com.example.PetApp.common.base.dto.MessageResponse;
import com.example.PetApp.common.base.util.AuthUtil;
import com.example.PetApp.domain.member.model.dto.request.*;
import com.example.PetApp.domain.member.model.dto.response.FindByIdResponseDto;
import com.example.PetApp.domain.member.model.dto.response.GetMemberResponseDto;
import com.example.PetApp.domain.member.model.dto.response.MemberSignResponseDto;
import com.example.PetApp.domain.member.model.dto.response.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입",
            description = "프로필 이미지는 선택, 기본이지미하고싶으면 send empty value 체크하지말고 그냥 요청"
    )
    @PostMapping(
            value = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MemberSignResponseDto> signUp(@ModelAttribute @Validated MemberSignDto memberSignDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(memberSignDto));
    }

    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
        return memberService.login(loginDto, response);
    }

    @GetMapping("/{memberId}")
    public GetMemberResponseDto getMember(@PathVariable Long memberId, Authentication authentication) {
        return memberService.getMember(memberId, AuthUtil.getEmail(authentication));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String accessToken) {
        memberService.logout(accessToken);
        return ResponseEntity.ok(new MessageResponse("로그아웃 되었습니다."));
    }

    @GetMapping("/find-id")
    public FindByIdResponseDto findById(@RequestParam String phoneNumber) {
        return memberService.findById(phoneNumber);
    }

    @PostMapping("/send-email")
    public ResponseEntity<MessageResponse> sendEmail(@RequestBody @Valid SendEmailDto sendEmailDto) {
        memberService.sendEmail(sendEmailDto);
        return ResponseEntity.ok(new MessageResponse("인증번호가 이메일로 전송되었습니다."));
    }

    @PostMapping("/verify-code")
    public AccessTokenResponseDto verifyCode(@RequestBody @Valid AuthCodeDto authCodeDto) {
        return memberService.verifyCode(authCodeDto.getEmail(), authCodeDto.getCode());
    }

    @PutMapping("/reset-password")//수정 필요 토큰 있을 때와 없을 때
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto, Authentication authentication) {
        memberService.resetPassword(resetPasswordDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 변경되었습나다."));
    }

    @DeleteMapping()
    public ResponseEntity<MessageResponse> deleteMember(Authentication authentication) {
        memberService.deleteMember(AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("탈퇴 되었습니다."));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<MessageResponse> createFcmToken(@RequestBody @Valid FcmTokenDto fcmTokenDto) {
        memberService.createFcmToken(fcmTokenDto);
        return ResponseEntity.ok(new MessageResponse("fcm토큰 생성완료."));
    }
}
