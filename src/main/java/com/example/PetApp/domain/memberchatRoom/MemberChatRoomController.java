package com.example.PetApp.domain.memberchatRoom;

import com.example.PetApp.common.app.common.MessageResponse;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.CreateMemberChatRoomResponseDto;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.MemberChatRoomsResponseDto;
import com.example.PetApp.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-chat-rooms")
public class MemberChatRoomController {
    private final MemberChatRoomService memberChatRoomService;

    @GetMapping
    public List<MemberChatRoomsResponseDto> getMemberChatRooms(Authentication authentication) {
        return memberChatRoomService.getMemberChatRooms(AuthUtil.getEmail(authentication));
    }

    @PostMapping
    public CreateMemberChatRoomResponseDto createMemberChatRoom(@RequestBody Long memberId, Authentication authentication) {
        return memberChatRoomService.createMemberChatRoom(memberId, AuthUtil.getEmail(authentication));
    }

    @PutMapping("/{memberChatRoomId}")
    public ResponseEntity<?> updateMemberChatRoom(@PathVariable Long memberChatRoomId, @RequestBody String memberChatRoomName, Authentication authentication) {
        memberChatRoomService.updateMemberChatRoom(memberChatRoomId, memberChatRoomName, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }

    @DeleteMapping("/{memberChatRoomId}")
    public ResponseEntity<?> deleteMemberChatRoom(@PathVariable Long memberChatRoomId, Authentication authentication) {
        memberChatRoomService.deleteMemberChatRoom(memberChatRoomId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @GetMapping("/{memberChatRoomId}")
    public ChatMessageResponseDto getMessages(@PathVariable Long memberChatRoomId,
                                              @RequestParam(defaultValue ="0") int page,
                                              Authentication authentication) {
        return memberChatRoomService.getMessages(memberChatRoomId, AuthUtil.getEmail(authentication), page);
    }
}
