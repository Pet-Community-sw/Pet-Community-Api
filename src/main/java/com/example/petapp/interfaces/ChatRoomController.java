package com.example.petapp.domain.chatroom;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatroom.dto.request.UpdateChatRoomDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "ChatRoom")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-rooms")
public class ChatRoomController {

    private final ChatRoomUseCase chatRoomUseCase;

    @Operation(
            summary = "채팅방 목록 조회"
    )
    @GetMapping()
    public List<ChatRoomResponseDto> chatRoomList(Authentication authentication) {
        return chatRoomUseCase.getChatRooms(AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "채팅방 채팅 내역 조회"
    )
    @GetMapping("/{chatRoomId}")
    public ChatMessageResponseDto getMessages(@PathVariable Long chatRoomId, @RequestParam(defaultValue = "0") int page, Authentication authentication) {
        return chatRoomUseCase.getMessages(chatRoomId, AuthUtil.getProfileId(authentication), page);
    }

    @Operation(
            summary = "유저가 마지막으로 읽은 메세지 이후 메시지 조회"
    )
    @GetMapping("/{chatRoomId}/after-messages")
    public ChatMessageResponseDto getAfterMessages(@PathVariable Long chatRoomId, @RequestParam Long lastSeq, Authentication authentication) {
        return chatRoomUseCase.getAfterMessages(chatRoomId, lastSeq, AuthUtil.getProfileId(authentication));
    }

    @Operation(
            summary = "채팅방 수정"
    )
    @PutMapping("/{chatRoomId}")
    public ResponseEntity<MessageResponse> updateChatRoom(@PathVariable Long chatRoomId, @RequestBody @Valid UpdateChatRoomDto updateChatRoomDto, Authentication authentication) {
        chatRoomUseCase.updateChatRoom(chatRoomId, updateChatRoomDto, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @Operation(
            summary = "채팅방 목록 삭제"
    )
    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<MessageResponse> deleteChatRoom(@PathVariable Long chatRoomId, Authentication authentication) {
        chatRoomUseCase.deleteChatRoom(chatRoomId, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }
}
