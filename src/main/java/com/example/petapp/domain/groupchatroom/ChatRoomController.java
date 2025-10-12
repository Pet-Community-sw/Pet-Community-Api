package com.example.petapp.domain.groupchatroom;

import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import com.example.petapp.domain.groupchatroom.model.dto.request.UpdateChatRoomDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatRoomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //변경해야됩니다. jwt토큰에 profileId집어 넣었어요
    @GetMapping()
    private List<ChatRoomResponseDto> chatRoomList(Authentication authentication) {
        return chatRoomService.getChatRooms(AuthUtil.getProfileId(authentication));
    }

    @GetMapping("/{chatRoomId}")
    private ChatMessageResponseDto getMessages(@PathVariable Long chatRoomId,
                                               @RequestParam(defaultValue = "0") int page,
                                               Authentication authentication
    ) {
        return chatRoomService.getMessages(chatRoomId, AuthUtil.getProfileId(authentication), page);
    }

    @PutMapping("/{chatRoomId}")
    private ResponseEntity<MessageResponse> updateChatRoom(@PathVariable Long chatRoomId, @RequestBody @Valid UpdateChatRoomDto updateChatRoomDto, Authentication authentication) {
        chatRoomService.updateChatRoom(chatRoomId, updateChatRoomDto, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{chatRoomId}")
    private ResponseEntity<MessageResponse> deleteChatRoom(@PathVariable Long chatRoomId, Authentication authentication) {
        chatRoomService.deleteChatRoom(chatRoomId, AuthUtil.getProfileId(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }
}
