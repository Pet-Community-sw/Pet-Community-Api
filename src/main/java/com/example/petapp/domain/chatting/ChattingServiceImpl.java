package com.example.petapp.domain.chatting;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.domain.chatting.mapper.ChatMessageMapper;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.ChatMessageDto;
import com.example.petapp.domain.chatting.model.dto.UserInfo;
import com.example.petapp.domain.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.offline.OfflineUserService;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
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

    private final ProfileQueryUseCase profileQueryUseCase;
    private final Map<CommandType, MessageTypeStrategy> messageTypeMap;
    private final QueryService queryService;
    private final MemberQueryUseCase memberQueryUseCase;
    private final OfflineUserService offlineUserService;

    @Transactional
    @Override
    public void sendToMessage(ChatMessageDto chatMessageDto, Long senderId) {
        log.info("[STOMP] messageMapping 시작 chatRoomId: {}, messageType: {}", chatMessageDto.getChatRoomId(), chatMessageDto.getCommandType());
        ChatRoom chatRoom = queryService.findByChatRoom(chatMessageDto.getChatRoomId());
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
                Profile profile = profileQueryUseCase.findOrThrow(senderId);
                userInfo = new UserInfo(profile.getPetName(), profile.getPetImageUrl());
            }
            case ONE -> {
                Member member = memberQueryUseCase.findOrThrow(senderId);
                userInfo = new UserInfo(member.getName(), member.getMemberImageUrl());
            }
        }
        ChatMessage chatMessage = ChatMessageMapper.toEntity(chatMessageDto, chatRoom, senderId, userInfo);
        offlineUserService.setOfflineUsersAndUnreadCount(chatMessage, chatRoom);
        return chatMessage;
    }
}
