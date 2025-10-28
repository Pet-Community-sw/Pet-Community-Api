package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.common.base.util.notification.SendNotificationUtil;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.dto.MessageResponseDto;
import com.example.petapp.domain.chatting.model.dto.UpdateListDto;
import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.chatting.model.type.MessageType;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class TalkStrategy implements MessageTypeStrategy {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final InMemoryService inMemoryService;
    private final SendNotificationUtil sendNotificationUtil;
    private final QueryService queryService;

    AtomicInteger seq = new AtomicInteger(0);

    @Override
    public void handle(ChatMessage chatMessage) {
        chatMessage.updateSeq(seq.incrementAndGet());
        chatMessageRepository.save(chatMessage);

        //메시지를 전송
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(),
                MessageResponseDto.builder().messageType(MessageType.TALK).body(chatMessage).build());

        sendChatNotificationAndUpdateList(chatMessage);
        inMemoryService.createLastMessageInfoData(chatMessage);
    }

    //todo : 업데이트 로직 정리해야됨.
    private void sendChatNotificationAndUpdateList(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";

        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        Set<Long> users = chatRoom.getUsers();
        Set<String> onlineUsers = inMemoryService.getOnlineDatas(chatRoomId);

        users.stream().filter(userId -> !userId.equals(senderId))
                .filter(userId -> !onlineUsers.contains(userId.toString()))
                .forEach(userId -> {
                    Profile profile = queryService.findByProfile(userId);
                    sendNotificationUtil.sendNotification(profile.getMember(), message);
                    int profileSeq = inMemoryService.getReadData(chatRoomId, profile.getId());
                    simpMessagingTemplate.convertAndSend("sub/list/" + profile.getMember().getId(),//todo : member와 profile 다르게 해야함.
                            MessageResponseDto.builder().messageType(MessageType.LIST_UPDATE).body(new UpdateListDto(chatRoomId, (chatMessage.getSeq() - profileSeq), chatMessage.getMessage(), chatMessage.getMessageTime())));
                });
    }
}
