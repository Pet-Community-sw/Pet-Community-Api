package com.example.petapp.domain.chatting.reader;

import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.chatting.model.dto.StompResponseDto;
import com.example.petapp.domain.chatting.model.dto.UpdateMessageDto;
import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.chatting.model.type.CommandType;
import com.example.petapp.domain.groupchatroom.mapper.ChatRoomMapper;
import com.example.petapp.domain.groupchatroom.model.dto.request.ChatMessageDtoMember;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.infrastructure.database.mongo.MongoService;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingReaderImpl implements ChattingReader {

    private final QueryService queryService;
    private final ChatMessageRepository chatMessageRepository;
    private final InMemoryService inMemoryService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MongoService mongoService;

    @Transactional
    @Override
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        log.info("getMessages 요청 chatRoomId : {}, userId :{}", chatRoomId, userId);

        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        chatRoom.validateUser(userId);

        Pageable pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "seq"));
        Page<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId, pageRequest);
        updateMessagesUnReadCount(chatRoomId, userId);

        List<ChatMessageDtoMember> chatMessageDtoMembers = ChatRoomMapper.toChatMessageDtos(messages.getContent());
        return new ChatMessageResponseDto(chatRoomId, chatMessageDtoMembers);
    }

    private void updateMessagesUnReadCount(Long chatRoomId, Long userId) {
        LastMessageInfoDto lastMessageInfoDto = inMemoryService.getLastMessageInfoData(userId);
        Long startSeq = inMemoryService.getReadData(chatRoomId, userId);
        Long endSeq = lastMessageInfoDto.getLastSeq();
        mongoService.updateMessages(chatRoomId, userId, startSeq, endSeq);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatRoomId,
                StompResponseDto.builder().commandType(CommandType.CHAT_UPDATE).body(new UpdateMessageDto(startSeq, endSeq)).build());
    }
}