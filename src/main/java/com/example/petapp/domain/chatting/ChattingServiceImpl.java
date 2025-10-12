package com.example.petapp.domain.chatting;

import com.example.petapp.domain.chatting.mapper.ChatMessageMapper;
import com.example.petapp.domain.chatting.model.dto.ChatMessageDto;
import com.example.petapp.domain.chatting.model.dto.UserInfo;
import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.chatting.model.type.MessageType;
import com.example.petapp.domain.chatting.offline.OfflineUserService;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingServiceImpl implements ChattingService {

    private final Map<MessageType, MessageTypeStrategy> messageTypeMap;
    private final QueryService queryService;
    private final OfflineUserService offlineUserService;

    @Transactional
    @Override
    public void sendToMessage(ChatMessageDto chatMessageDto, Long senderId) {
        log.info("[STOMP] messageMapping 시작 chatRoomId: {}, messageType: {}", chatMessageDto.getChatRoomId(), chatMessageDto.getMessageType());

        ChatRoom chatRoom = queryService.findByChatRoom(chatMessageDto.getChatRoomId());

        ChatMessage chatMessage = getChatMessage(chatMessageDto, senderId, chatRoom);
        chatMessageDto.checkSeq(chatMessage);

        MessageTypeStrategy messageTypeStrategy = messageTypeMap.get(chatMessageDto.getMessageType());
        if (messageTypeStrategy == null) {
            throw new IllegalArgumentException("[ERROR] : messageType 외 요청");
        }
        messageTypeStrategy.handle(chatMessage);
    }

    private ChatMessage getChatMessage(ChatMessageDto chatMessageDto, Long senderId, ChatRoom chatRoom) {
        UserInfo userInfo = null;
        switch (chatRoom.getChatRoomType()) {
            case MANY -> {
                Profile profile = queryService.findByProfile(senderId);
                userInfo = new UserInfo(profile.getPetName(), profile.getPetImageUrl());
            }
            case ONE -> {
                Member member = queryService.findByMember(senderId);
                userInfo = new UserInfo(member.getName(), member.getMemberImageUrl());
            }
        }
        ChatMessage chatMessage = ChatMessageMapper.toEntity(chatMessageDto, chatRoom, senderId, userInfo);
        offlineUserService.setOfflineUsersAndUnreadCount(chatMessage, chatRoom);
        return chatMessage;
    }
}
