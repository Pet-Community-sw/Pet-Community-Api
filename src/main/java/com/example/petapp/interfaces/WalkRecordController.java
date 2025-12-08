package com.example.petapp.interfaces;

import com.example.petapp.application.in.walkrecord.WalkRecordUseCase;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordLocationResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordResponseDto;
import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WalkRecord")
@RestController
@RequiredArgsConstructor
@RequestMapping("/walk-record")//삭제도 있어야하나?
public class WalkRecordController {
    private final WalkRecordUseCase walkRecordUseCase;

    @Operation(
            summary = "산책 기록 상세 조회"
    )
    @GetMapping("/{walkRecordId}")
    public GetWalkRecordResponseDto getWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordUseCase.getWalkRecord(walkRecordId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "대리 산책자 실시간 위치 정보 조회"
    )
    @GetMapping("/{walkRecordId}/location")
    public GetWalkRecordLocationResponseDto getWalkRecordLocation(@PathVariable Long walkRecordId, Authentication authentication) {
        return walkRecordUseCase.getWalkRecordLocation(walkRecordId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "산책 기록 시작"
    )
    @PutMapping("/{walkRecordId}/start")
    public ResponseEntity<MessageResponse> updateStartWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        walkRecordUseCase.updateStartWalkRecord(walkRecordId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("start"));
    }

    @Operation(
            summary = "산책 기록 끝"
    )
    @PutMapping("/{walkRecordId}/finish")
    public ResponseEntity<MessageResponse> updateFinishWalkRecord(@PathVariable Long walkRecordId, Authentication authentication) {
        walkRecordUseCase.FinishWalkRecord(walkRecordId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("finish"));
    }
}
