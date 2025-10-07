package com.example.PetApp.domain.chatting.handler;

import com.example.PetApp.domain.chatting.mapper.ChatMessageMapper;
import com.example.PetApp.domain.chatting.model.dto.ChatMessageDto;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.chatting.offline.OfflineUserService;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomHandlerImp implements ChatRoomHandler {
    private final QueryService queryService;
    private final OfflineUserService offlineUserService;

    @Override
    public void handleGroupChat(ChatMessageDto chatMessageDto, Long senderId) {
        Profile profile = queryService.findByProfile(senderId);
        ChatRoom chatRoom = queryService.findByChatRoom(chatMessageDto.getChatRoomId());
        chatRoom.validateProfile(profile);
        ChatMessage chatMessage = ChatMessageMapper.toEntity(chatMessageDto, senderId, profile.getPetName(), profile.getPetImageUrl());

        offlineUserService.setOfflineUsersAndUnreadCount(chatMessage, chatRoom);
    }

    @Override
    public void handleOneToOneChat(ChatMessageDto chatMessageDto, Long senderId) {
        Member member = queryService.findByMember(senderId);
        MemberChatRoom memberChatRoom = queryService.findByMemberChatRoom(chatMessageDto.getChatRoomId());
        memberChatRoom.validateMember(member);
        ChatMessage chatMessage = ChatMessageMapper.toEntity(chatMessageDto, senderId, member.getName(), member.getMemberImageUrl());

        offlineUserService.setOfflineUsersAndUnreadCount(chatMessage, memberChatRoom);
    }
}
