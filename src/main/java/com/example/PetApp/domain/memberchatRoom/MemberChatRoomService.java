package com.example.PetApp.domain.memberchatRoom;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.CreateMemberChatRoomResponseDto;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.MemberChatRoomsResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemberChatRoomService {
    List<MemberChatRoomsResponseDto> getMemberChatRooms(String email);

    CreateMemberChatRoomResponseDto createMemberChatRoom(Member fromMember, Member member);

    CreateMemberChatRoomResponseDto createMemberChatRoom(Long memberId, String email);

    void updateMemberChatRoom(Long memberChatRoomId, String userChatRoomName, String email);

    void deleteMemberChatRoom(Long userChatRoomId, String email);

    ChatMessageResponseDto getMessages(Long memberChatRoomId, String email, int page);

}
