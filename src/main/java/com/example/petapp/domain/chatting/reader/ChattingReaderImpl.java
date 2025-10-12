package com.example.petapp.domain.chatting.reader;

import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.dto.MessageResponseDto;
import com.example.petapp.domain.chatting.model.dto.UpdateMessageDto;
import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.chatting.model.type.MessageType;
import com.example.petapp.domain.groupchatroom.mapper.ChatRoomMapper;
import com.example.petapp.domain.groupchatroom.model.dto.request.ChatMessageDtoMember;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.infrastructure.database.mongo.MongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingReaderImpl implements ChattingReader {

    private final QueryService queryService;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MongoService mongoService;
    private final StringRedisTemplate redisTemplate;

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
        Map<Object, Object> lastMessageInfo = redisTemplate.opsForHash().entries("chat:lastMessageInfo:" + chatRoomId);
        Object seqByUser = redisTemplate.opsForHash().get("chatRoomId:" + chatRoomId + ":read", String.valueOf(userId));
        int startSeq = seqByUser == null ? 0 : (Integer) (seqByUser);
        int endSeq = (Integer) lastMessageInfo.getOrDefault("seq", null);
        mongoService.updateMessages(chatRoomId, userId, startSeq, endSeq);
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatRoomId,
                MessageResponseDto.builder().messageType(MessageType.CHAT_UPDATE).body(new UpdateMessageDto(startSeq, endSeq)).build());
    }
}

