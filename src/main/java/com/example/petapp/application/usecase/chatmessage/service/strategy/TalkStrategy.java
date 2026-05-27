package com.example.petapp.application.usecase.chatmessage.service.strategy;

import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.application.usecase.chatmessage.MessageTypeStrategy;
import com.example.petapp.application.usecase.chatmessage.model.dto.SendResponseDto;
import com.example.petapp.application.usecase.chatmessage.model.dto.UpdateListDto;
import com.example.petapp.application.usecase.chatmessage.model.type.CommandType;
import com.example.petapp.application.usecase.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.application.usecase.notification.dto.NotificationEvent;
import com.example.petapp.application.usecase.profile.ProfileQueryUseCase;
import com.example.petapp.domain.chatmessage.ChatMessageRepository;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.infrastructure.stomp.store.ChatOnlineStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TalkStrategy implements MessageTypeStrategy {

    private final ChatRoomQueryUseCase chatRoomQueryUseCase;
    private final ProfileQueryUseCase profileQueryUseCase;
    private final SendPort sendPort;
    private final ChatMessageRepository chatMessageRepository;
    private final SeqCachePort seqCachePort;
    private final ChatOnlineStore chatOnlineStore;
    private final LastMessageCachePort lastMessageCachePort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void handle(ChatMessage chatMessage) {
        boolean isExist = seqCachePort.exists(chatMessage.getChatRoomId());
        if (!isExist) {
            Long LastMessageSeq = chatMessageRepository.findCurrent(chatMessage.getChatRoomId())
                    .map(ChatMessage::getSeq).orElse(0L);

            seqCachePort.initializeIfAbsent(chatMessage.getChatRoomId(), LastMessageSeq);
        }
        Long seq = seqCachePort.incrementAndGet(chatMessage.getChatRoomId());
        chatMessage.updateSeq(seq);
        chatMessageRepository.save(chatMessage);

        //메시지를 전송
        sendPort.send("/sub/chat/" + chatMessage.getChatRoomId(),
                SendResponseDto.builder().commandType(CommandType.TALK).body(chatMessage).build());

        sendChatNotificationAndUpdateList(chatMessage);
        lastMessageCachePort.saveLastMessage(chatMessage);
    }

    @Override
    public CommandType getCommand() {
        return CommandType.TALK;
    }

    private void sendChatNotificationAndUpdateList(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";

        ChatRoom chatRoom = chatRoomQueryUseCase.find(chatRoomId);
        Set<Long> users = chatRoom.getUsers();
        Set<Long> onlineUsers = chatOnlineStore.getOnlineUserList(chatRoomId);

        users.stream().filter(userId -> !userId.equals(senderId))
                .filter(userId -> !onlineUsers.contains(userId))
                .forEach(userId -> {
                    Profile profile = profileQueryUseCase.findOrThrow(userId);
                    eventPublisher.publishEvent(new NotificationEvent(profile.getMember().getId(), message));

                    sendPort.send("/sub/list/" + profile.getMember().getId(),
                            SendResponseDto.builder().commandType(CommandType.LIST_UPDATE).body(new UpdateListDto(chatRoomId, chatMessage.getMessage(), chatMessage.getMessageTime())).build());
                });
    }
}
