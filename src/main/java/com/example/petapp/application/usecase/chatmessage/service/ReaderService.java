package com.example.petapp.application.usecase.chatmessage.service;

import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.usecase.chatmessage.ReaderUseCase;
import com.example.petapp.application.usecase.chatmessage.model.dto.LastMessageInfoDto;
import com.example.petapp.application.usecase.chatroom.dto.request.ChatMessageDtoMember;
import com.example.petapp.application.usecase.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.application.usecase.chatroom.mapper.ChatRoomMapper;
import com.example.petapp.domain.chatmessage.ChatMessageRepository;
import com.example.petapp.domain.chatmessage.model.ChatMessage;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderService implements ReaderUseCase {

    private final ChatMessageRepository chatMessageRepository;
    private final ReadMessageCachePort readMessageCachePort;
    private final LastMessageCachePort lastMessageCachePort;

    @Transactional
    @Override
    public ChatMessageResponseDto getMessages(ChatRoom chatRoom, Long userId, int page) {
        Long chatRoomId = chatRoom.getId();
        Pageable pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "seq"));
        Page<ChatMessage> messages = chatMessageRepository.findAll(chatRoomId, pageRequest);//seq로 정렬 redis원자적연산인 seq로 정렬 순서 보장
        updateReadSeq(chatRoomId, userId);

        List<ChatMessageDtoMember> chatMessageDtoMembers = ChatRoomMapper.toChatMessageDtos(messages.getContent());
        return new ChatMessageResponseDto(chatRoomId, chatMessageDtoMembers);
    }

    /**
     * 유저의 웹소켓이 disconnect되고 다시 connect되는 사이에 유실되는 메세지 응답
     */
    @Transactional
    @Override
    public ChatMessageResponseDto getAfterMessages(ChatRoom chatRoom, Long lastSeq, Long userId) {
        Long chatRoomId = chatRoom.getId();
        List<ChatMessage> afterMessages = chatMessageRepository.findAllBySeq(chatRoomId, lastSeq);
        updateReadSeq(chatRoomId, userId);

        return new ChatMessageResponseDto(chatRoomId, ChatRoomMapper.toChatMessageDtos(afterMessages));
    }

    private void updateReadSeq(Long chatRoomId, Long userId) {
        LastMessageInfoDto lastMessageInfoDto = lastMessageCachePort.findLastMessageInfo(chatRoomId);
        if (lastMessageInfoDto.getLastSeq() > 0) {
            readMessageCachePort.markAsRead(ChatMessage.builder()
                    .chatRoomId(chatRoomId)
                    .senderId(userId)
                    .seq(lastMessageInfoDto.getLastSeq())
                    .build());
        }
    }
}
