package com.example.petapp.application.service.chatting;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatting.ChattingUseCase;
import com.example.petapp.application.in.chatting.MessageTypeStrategy;
import com.example.petapp.application.in.chatting.OfflineUserUseCase;
import com.example.petapp.application.in.chatting.mapper.ChatMessageMapper;
import com.example.petapp.application.in.chatting.model.dto.ChatMessageDto;
import com.example.petapp.application.in.chatting.model.dto.UserInfo;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService implements ChattingUseCase {

    private final ProfileUseCase profileUseCase;
    private final Map<CommandType, MessageTypeStrategy> messageTypeMap;
    private final ChatRoomUseCase chatRoomUseCase;
    private final MemberUseCase memberUseCase;
    private final OfflineUserUseCase offlineUserUseCase;

    @Transactional
    @Override
    public void sendToMessage(ChatMessageDto chatMessageDto, Long senderId) {
        log.info("[STOMP] 일반 메시지 전송 chatRoomId: {}, messageType: {}", chatMessageDto.getChatRoomId(), chatMessageDto.getCommandType());
        ChatRoom chatRoom = chatRoomUseCase.find(chatMessageDto.getChatRoomId());
        chatRoom.validateUser(senderId);
        ChatMessage chatMessage = getChatMessage(chatMessageDto, senderId, chatRoom);
//        chatMessage.checkSeq();//?
        MessageTypeStrategy messageTypeStrategy = messageTypeMap.get(chatMessageDto.getCommandType());
        if (messageTypeStrategy == null) {
            throw new IllegalArgumentException("[ERROR] : messageType 외 요청");
        }
        messageTypeStrategy.handle(chatMessage);
    }


    private ChatMessage getChatMessage(ChatMessageDto chatMessageDto, Long senderId, ChatRoom chatRoom) {
        UserInfo userInfo = null;
        switch (chatRoom.getChatRoomType()) {
            case MANY -> {
                Profile profile = profileUseCase.findOrThrow(senderId);
                userInfo = new UserInfo(profile.getPetName(), profile.getPetImageUrl());
            }
            case ONE -> {
                Member member = memberUseCase.findOrThrow(senderId);
                userInfo = new UserInfo(member.getName(), member.getMemberImageUrl());
            }
        }
        ChatMessage chatMessage = ChatMessageMapper.toEntity(chatMessageDto, chatRoom, senderId, userInfo);
        offlineUserUseCase.setOfflineUsersAndUnreadCount(chatMessage, chatRoom);
        return chatMessage;
    }
}
