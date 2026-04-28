package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatting.MessageTypeStrategy;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.dto.UpdateListDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.AckInfoRepository;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.domain.profile.model.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TalkStrategy implements MessageTypeStrategy {

    private final ChatRoomUseCase chatRoomUseCase;
    private final ProfileUseCase profileUseCase;
    private final SendPort sendPort;
    private final ChatMessageRepository chatMessageRepository;
    private final SeqCachePort seqCachePort;
    private final ChatOnlineCachePort chatOnlineCachePort;
    private final ReadMessageCachePort readMessageCachePort;
    private final LastMessageCachePort lastMessageCachePort;
    private final AckInfoRepository ackInfoRepository;
    private final SimpUserRegistry simpUserRegistry;
    private final TaskScheduler resendScheduler;//stompConfig에서 선언해놓았던 스케줄러가 선언이됨.
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void handle(ChatMessage chatMessage) {
        boolean isExist = seqCachePort.exist(chatMessage.getChatRoomId());
        if (!isExist) {
            Long LastMessageSeq = chatMessageRepository.findCurrent(chatMessage.getChatRoomId())
                    .map(ChatMessage::getSeq).orElse(0L);

            seqCachePort.create(chatMessage.getChatRoomId(), LastMessageSeq);//seq가 0일수도있고 아닐수도있음.
        }
        Long seq = seqCachePort.increment(chatMessage.getChatRoomId());
        chatMessage.updateSeq(seq);
        chatMessageRepository.save(chatMessage);

        //메시지를 전송
        sendPort.send("/sub/chat/" + chatMessage.getChatRoomId(),
                SendResponseDto.builder().commandType(CommandType.TALK).body(chatMessage).build());

        scheduleRetry(chatMessage);
        sendChatNotificationAndUpdateList(chatMessage);
        lastMessageCachePort.create(chatMessage);
    }

    @Override
    public CommandType getCommand() {
        return CommandType.TALK;
    }

    private void sendChatNotificationAndUpdateList(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";

        ChatRoom chatRoom = chatRoomUseCase.find(chatRoomId);
        Set<Long> users = chatRoom.getUsers();
        Set<String> onlineUsers = chatOnlineCachePort.find(chatRoomId);

        users.stream().filter(userId -> !userId.equals(senderId))
                .filter(userId -> !onlineUsers.contains(userId.toString()))
                .forEach(userId -> {
                    Profile profile = profileUseCase.findOrThrow(userId);
                    eventPublisher.publishEvent(new NotificationEvent(profile.getMember().getId(), message));

                    Long profileSeq = readMessageCachePort.find(chatRoomId, profile.getId());
                    sendPort.send("/sub/list/" + profile.getMember().getId(),
                            SendResponseDto.builder().commandType(CommandType.LIST_UPDATE).body(new UpdateListDto(chatRoomId, (chatMessage.getSeq() - profileSeq), chatMessage.getMessage(), chatMessage.getMessageTime())).build());
                });
    }

    /**
     * 재전송 로직 한번만 재전송을함.
     */
    private void scheduleRetry(ChatMessage chatMessage) {
        Set<Long> unReadUsers = chatMessage.getUsers();
        ChatRoom chatRoom = chatRoomUseCase.find(chatMessage.getChatRoomId());
        Set<Long> sendUsers = chatRoom.getUsers().stream().filter(userId -> !unReadUsers.contains(userId)).collect(Collectors.toSet());
        ackInfoRepository.save(chatMessage.getClientMessageId(), sendUsers);

        resendScheduler.schedule(
                () -> retrySend(chatMessage),// 실행할 작업
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
            sendPort.sendToUser(
                    userId.toString(), "/sub/chat",
                    SendResponseDto.builder().commandType(CommandType.TALK).body(chatMessage).build());
        }
        ackInfoRepository.clear(chatMessage.getClientMessageId());
    }
}
