package com.example.petapp.interfaces;

import com.example.petapp.application.common.AuthUtil;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.member.object.dto.request.*;
import com.example.petapp.application.in.member.object.dto.response.*;
import com.example.petapp.interfaces.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "Member")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @Operation(
            summary = "회원가입",
            description = "프로필 이미지는 선택, 기본이지미하고싶으면 send empty value 체크하지말고 그냥 요청"
    )
    @PostMapping(
            value = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )//Todo : validated, valid
    public ResponseEntity<MemberSignResponseDto> signUp(@ModelAttribute @Validated MemberSignDto memberSignDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberUseCase.create(memberSignDto));
    }

    @Operation(
            summary = "로그인"
    )
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {
        return memberUseCase.login(loginDto, response);
    }

    @Operation(
            summary = "유저 상세 조회"
    )
    @GetMapping("/{targetId}")
    public GetMemberResponseDto getMember(@PathVariable Long targetId, Authentication authentication) {
        return memberUseCase.get(targetId, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "로그아웃"
    )
    @DeleteMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Parameter(hidden = true) @RequestHeader("Authorization") String accessToken) {
        memberUseCase.logout(accessToken);
        return ResponseEntity.ok(new MessageResponse("로그아웃 되었습니다."));
    }

    @Operation(
            summary = "아이디 찾기"
    )
    @GetMapping("/find-id")
    public FindByIdResponseDto findById(@RequestParam String phoneNumber) {
        return memberUseCase.findById(phoneNumber);
    }

    @Operation(
            summary = "이메일 인증코드 요청"
    )
    @PostMapping("/send-email")
    public ResponseEntity<MessageResponse> sendEmail(@RequestBody @Valid SendEmailDto sendEmailDto) {
        memberUseCase.sendEmail(sendEmailDto);
        return ResponseEntity.ok(new MessageResponse("인증번호가 이메일로 전송되었습니다."));
    }

    @Operation(
            summary = "인증코드 검증"
    )
    @PostMapping("/verify-code")
    public AccessTokenResponseDto verifyCode(@RequestBody @Valid AuthCodeDto authCodeDto) {
        return memberUseCase.verifyCode(authCodeDto);
    }

    @Operation(
            summary = "비밀번호 재설정"
    )
    @PutMapping("/reset-password")//수정 필요 토큰 있을 때와 없을 때
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto, Authentication authentication) {
        memberUseCase.resetPassword(resetPasswordDto, AuthUtil.getMemberId(authentication));
        return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 변경되었습나다."));
    }

    @Operation(
            summary = "회원 수정"
    )
    @PutMapping(
            value = "/members",
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
            summary = "자동완성 검색"
    )
    @GetMapping("/auto-complete")
    public List<MemberSearchResponseDto> autoComplete(@RequestParam String keyword, Authentication authentication) {
        return memberUseCase.autoComplete(keyword, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "검색"
    )
    @GetMapping("/search")
    public List<MemberSearchResponseDto> search(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, Authentication authentication) {
        return memberUseCase.search(keyword, page, AuthUtil.getMemberId(authentication));
    }

    @Operation(
            summary = "fcm토큰 생성"
    )
    @PostMapping("/fcm-token")
    public ResponseEntity<MessageResponse> createFcmToken(@RequestBody @Valid FcmTokenDto fcmTokenDto) {
        memberUseCase.createFcmToken(fcmTokenDto);
        return ResponseEntity.ok(new MessageResponse("fcm토큰 생성완료."));
    }
}
