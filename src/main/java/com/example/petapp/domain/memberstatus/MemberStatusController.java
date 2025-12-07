package com.example.petapp.domain.memberstatus;

import com.example.petapp.common.base.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserStatus")
@RestController
@RequestMapping("/status")
@RequiredArgsConstructor //todo : 웹소켓 connect시 로 해보자
public class MemberStatusController {

    private final MemberStatusService memberStatusService;

    @Operation(
            summary = "유저 상태 변경(foreground or background)"
    )
    @PostMapping
    public ResponseEntity<MessageResponse> MemberStatus(Authentication authentication) {
        return ResponseEntity.ok(memberStatusService.updateMemberStatus(authentication.getPrincipal().toString()));
    }
}
