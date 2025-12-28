package com.example.petapp.application.service.chatting;

import com.example.petapp.application.in.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.application.in.chatroom.dto.request.ChatMessageDtoMember;
import com.example.petapp.application.in.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.application.in.chatroom.mapper.ChatRoomMapper;
import com.example.petapp.application.in.chatting.ReaderUseCase;
import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.dto.UpdateMessageDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.infrastructure.database.mongo.MongoService;
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
    private final ChatRoomQueryUseCase chatRoomQueryUseCase;
    private final SendPort sendPort;
    private final MongoService mongoService;
    private final ReadMessageCachePort readMessageCachePort;
    private final LastMessageCachePort lastMessageCachePort;

    @Transactional
    @Override
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        ChatRoom chatRoom = chatRoomQueryUseCase.find(chatRoomId);
        chatRoom.validateUser(userId);

        Pageable pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "seq"));
        Page<ChatMessage> messages = chatMessageRepository.findAll(chatRoomId, pageRequest);//seq로 정렬 redis원자적연산인 seq로 정렬 순서 보장
        updateMessagesUnReadCount(chatRoomId, userId);

        List<ChatMessageDtoMember> chatMessageDtoMembers = ChatRoomMapper.toChatMessageDtos(messages.getContent());
        return new ChatMessageResponseDto(chatRoomId, chatMessageDtoMembers);
    }

    /**
     * 유저의 웹소켓이 disconnect되고 다시 connect되는 사이에 유실되는 메세지 응답
     */
    @Transactional
    @Override
    public ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long userId) {
        ChatRoom chatRoom = chatRoomQueryUseCase.find(chatRoomId);
        chatRoom.validateUser(userId);

        List<ChatMessage> afterMessages = chatMessageRepository.findAllBySeq(chatRoomId, lastSeq);
        updateMessagesUnReadCount(chatRoomId, userId);

        return new ChatMessageResponseDto(chatRoomId, ChatRoomMapper.toChatMessageDtos(afterMessages));
    }

    /**
     * 채팅 내역 조회시 채팅 메세지의 안읽은 수 업데이트
     */
    private void updateMessagesUnReadCount(Long chatRoomId, Long userId) {
        LastMessageInfoDto lastMessageInfoDto = lastMessageCachePort.find(userId);
        Long startSeq = readMessageCachePort.find(chatRoomId, userId);
        Long endSeq = lastMessageInfoDto.getLastSeq();
        mongoService.updateMessages(chatRoomId, userId, startSeq, endSeq);
        sendPort.send("/sub/chat/" + chatRoomId,
                SendResponseDto.builder().commandType(CommandType.CHAT_UPDATE).body(new UpdateMessageDto(startSeq, endSeq)).build());
    }
}