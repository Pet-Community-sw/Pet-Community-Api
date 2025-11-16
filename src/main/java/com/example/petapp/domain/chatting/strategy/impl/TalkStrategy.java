package com.example.petapp.domain.chatting.strategy.impl;

import com.example.petapp.common.base.util.notification.SendNotificationUtil;
import com.example.petapp.domain.chatting.AckInfoRepository;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.chatting.model.dto.StompResponseDto;
import com.example.petapp.domain.chatting.model.dto.UpdateListDto;
import com.example.petapp.domain.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TalkStrategy implements MessageTypeStrategy {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final InMemoryService inMemoryService;
    private final SendNotificationUtil sendNotificationUtil;
    private final QueryService queryService;
    private final AckInfoRepository ackInfoRepository;
    private final SimpUserRegistry simpUserRegistry;
    private final TaskScheduler resendScheduler;//stompConfig에서 선언해놓았던 스케줄러가 선언이됨.

    @Override
    public void handle(ChatMessage chatMessage) {
        boolean isExist = inMemoryService.existRoomSeq(chatMessage.getChatRoomId());
        if (!isExist) {
            Long LastMessageSeq = chatMessageRepository.findFirstByChatRoomIdOrderBySeqDesc(chatMessage.getChatRoomId())
                    .map(ChatMessage::getSeq).orElse(0L);

            inMemoryService.createRoomSeq(chatMessage.getChatRoomId(), LastMessageSeq);//seq가 0일수도있고 아닐수도있음.
        }
        Long seq = inMemoryService.incrementSeq(chatMessage.getChatRoomId());
        chatMessage.updateSeq(seq);
        chatMessageRepository.save(chatMessage);

        //메시지를 전송
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(),
                StompResponseDto.builder().commandType(CommandType.TALK).body(chatMessage).build());

        scheduleRetry(chatMessage);
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
                    Long profileSeq = inMemoryService.getReadData(chatRoomId, profile.getId());
                    simpMessagingTemplate.convertAndSend("sub/list/" + profile.getMember().getId(),//todo : member와 profile 다르게 해야함.
                            StompResponseDto.builder().commandType(CommandType.LIST_UPDATE).body(new UpdateListDto(chatRoomId, (chatMessage.getSeq() - profileSeq), chatMessage.getMessage(), chatMessage.getMessageTime())));
                });
    }

    /**
     * 재전송 로직 한번만 재전송을함.
     */
    private void scheduleRetry(ChatMessage chatMessage) {
        Set<Long> unReadUsers = chatMessage.getUsers();
        ChatRoom chatRoom = queryService.findByChatRoom(chatMessage.getChatRoomId());
        Set<Long> sendUsers = chatRoom.getUsers().stream().filter(userId -> !unReadUsers.contains(userId)).collect(Collectors.toSet());
        ackInfoRepository.save(chatMessage.getClientMessageId(), sendUsers);

        resendScheduler.schedule(
                () -> retrySend(chatMessage),                              // 실행할 작업
                new Date(System.currentTimeMillis() + 1000) // 1초 후 실행
        );
    }

    private void retrySend(ChatMessage chatMessage) {

        Set<Long> unAckedUsers = ackInfoRepository.find(chatMessage.getClientMessageId());
        if (unAckedUsers.isEmpty()) {
            return;
        }
        // 재전송 로직
        for (Long userId : unAckedUsers) {
            if (simpUserRegistry.getUser(userId.toString()) == null) {
                continue; //unsubscribe는 패스
            }
            simpMessagingTemplate.convertAndSendToUser(
                    userId.toString(), "/sub/chat",
                    StompResponseDto.builder().commandType(CommandType.TALK).body(chatMessage).build());
        }
        ackInfoRepository.clear(chatMessage.getClientMessageId());
    }
}
