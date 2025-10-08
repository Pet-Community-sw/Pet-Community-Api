package com.example.PetApp.domain.chatting.reader;

import com.example.PetApp.domain.chatting.ChatMessageRepository;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.groupchatroom.mapper.ChatRoomMapper;
import com.example.PetApp.domain.groupchatroom.model.dto.request.ChatMessageDtoMember;
import com.example.PetApp.domain.groupchatroom.model.dto.request.UpdateChatUnReadCountDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingReaderImpl implements ChattingReader {

    private final QueryService queryService;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @Override
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        log.info("getMessages 요청 chatRoomId : {}, userId :{}", chatRoomId, userId);

        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        chatRoom.validateUser(userId);

        Pageable pageRequest = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "seq"));
        Page<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId, pageRequest);
        ChatMessage chatMessage = messages.getContent().get(0);
        updateLastUserInfo(chatMessage);
        updateProfilesForMessages(messages.getContent(), userId);

        List<ChatMessageDtoMember> chatMessageDtoMembers = ChatRoomMapper.toChatMessageDtos(messages.getContent());
        return new ChatMessageResponseDto(chatRoomId, chatMessageDtoMembers);
    }

    private void updateProfilesForMessages(List<ChatMessage> messages, Long userId) {
        for (ChatMessage chatMessage : messages) {
            List<Long> offlineUsers = chatMessage.getUsers();

            // 자신을 제외한 리스트로 새로 만듦
            List<Long> updatedOfflineProfiles = offlineUsers.stream()
                    .filter(id -> !id.equals(userId))
                    .collect(Collectors.toList());

            // 업데이트된 리스트 세팅
            chatMessage.setUsers(updatedOfflineProfiles);

            chatMessage.setChatUnReadCount(chatMessage.getUsers().size());

            chatMessageRepository.save(chatMessage);//카톡처럼 많은 트래픽이 발생안할것같아 이렇게함.

            UpdateChatUnReadCountDto updateChatUnReadCountDto = ChatRoomMapper.toUpdateChatUnReadCountDto(chatMessage);

            simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getChatRoomId(), updateChatUnReadCountDto);
            //이거 api명세서 작성해야됨. 안읽은 수 처리.
        }
    }

    //todo : 공통 로직 모듈화.
    private void updateLastUserInfo(ChatMessage chatMessage) {
        Map<String, String> lastMessageInfo = new HashMap<>();
        lastMessageInfo.put("seq", String.valueOf(chatMessage.getSeq()));
        lastMessageInfo.put("lastMessage", chatMessage.getMessage());
        lastMessageInfo.put("lastMessageTime", String.valueOf(chatMessage.getMessageTime()));
        redisTemplate.opsForHash().putAll("chat:lastMessageInfo:" + chatMessage.getChatRoomId(), lastMessageInfo);
    }
}
