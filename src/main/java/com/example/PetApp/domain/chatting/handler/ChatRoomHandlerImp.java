package com.example.PetApp.domain.chatting.handler;

import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.groupchatroom.ChatRoomRepository;
import com.example.PetApp.domain.memberchatRoom.MemberChatRoomRepository;
import com.example.PetApp.domain.member.MemberRepository;
import com.example.PetApp.domain.profile.ProfileRepository;
import com.example.PetApp.domain.chatting.offline.OfflineUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomHandlerImp implements ChatRoomHandler{
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final OfflineUserService offlineUserService;

    @Override
    public void handleGroupChat(ChatMessage chatMessage, Long senderId) {
        ChatRoomAccess result = verifyChatRoomAccess(chatMessage.getChatRoomId(), senderId);

        chatMessage.setSenderImageUrl(result.profile.getPetImageUrl());
        chatMessage.setSenderName(result.profile.getPetName());

        offlineUserService.setOfflineProfilesAndUnreadCount(chatMessage, result.chatRoom);
    }

    @Override
    public void handleOneToOneChat(ChatMessage chatMessage, Long senderId) {
        MemberChatRoomAccess memberChatRoomAccess = verifyMemberChatRoomAccess(chatMessage.getChatRoomId(), senderId);

        chatMessage.setSenderImageUrl(memberChatRoomAccess.member.getMemberImageUrl());
        chatMessage.setSenderName(memberChatRoomAccess.member.getName());

        offlineUserService.setOfflineMembersAndUnreadCount(chatMessage, memberChatRoomAccess.memberChatRoom);
    }

    @Override
    public ChatRoomAccess verifyChatRoomAccess(Long chatRoomId, Long senderId) {
        Profile profile = profileRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        if (!chatRoom.getProfiles().contains(profile)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        return new ChatRoomAccess(profile, chatRoom);
    }

    @Override
    public MemberChatRoomAccess verifyMemberChatRoomAccess(Long chatRoomId, Long senderId) {
        Member member = memberRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        if (!memberChatRoom.getMembers().contains(member)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        return new MemberChatRoomAccess(member, memberChatRoom);
    }

    @AllArgsConstructor
    public static class MemberChatRoomAccess {
        public final Member member;
        public final MemberChatRoom memberChatRoom;
    }

    @AllArgsConstructor
    public static class ChatRoomAccess {
        public final Profile profile;
        public final ChatRoom chatRoom;
    }
}
