package com.example.PetApp.domain.groupchatroom;

import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.domain.chatting.ChatMessageRepository;
import com.example.PetApp.domain.chatting.ChattingReader;
import com.example.PetApp.domain.chatting.model.type.ChatRoomType;
import com.example.PetApp.domain.groupchatroom.mapper.ChatRoomMapper;
import com.example.PetApp.domain.groupchatroom.model.dto.request.UpdateChatRoomDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatRoomResponseDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.profile.model.dto.response.ChatRoomUsersResponseDto;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.query.QueryService;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
/*
 *   todo : 지금은 profile 채팅방만 구현해놨음.
 * */
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChattingReader chattingReader;
    private final StringRedisTemplate redisTemplate;
    private final QueryService queryService;

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomResponseDto> getChatRooms(Long profileId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserIdAndChatRoomType(profileId, ChatRoomType.MANY);//나중에 타입 파라미터로 방아야함
        return chatRoomList.stream()
                .map(chatRoom -> toChatRoomsResponseDtoWithRedis(chatRoom, profileId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto createChatRoom(WalkingTogetherMatch walkingTogetherMatch, Profile profile) {
        Optional<ChatRoom> chatRoom1 = chatRoomRepository.findByWalkingTogetherMatch(walkingTogetherMatch);
        if (chatRoom1.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom chatRoom = ChatRoomMapper.toEntity(walkingTogetherMatch, profile);
            ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
            return new CreateChatRoomResponseDto(savedChatRoom.getId(), true);
        } else {
            if (walkingTogetherMatch.getLimitCount() <= chatRoom1.get().getUsers().size()) {
                throw new ConflictException("인원초과");//채팅방 limitCount설정.
            }
            ChatRoom chatRoom = chatRoom1.get();
            chatRoom.addUser(profile.getId());
            return new CreateChatRoomResponseDto(chatRoom.getId(), false);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getUsers(Long chatRoomId) {
        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        return new ArrayList<>(chatRoom
                .getUsers());
    }

    @Transactional
    @Override
    public void deleteChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        chatRoom.validateUser(userId);
        chatRoom.deleteUser(userId);
        redisTemplate.opsForHash().delete("chatRoomId:" + chatRoom + ":read", String.valueOf(userId));
        if (chatRoomRepository.countByProfile(chatRoomId) <= 1) {//방 사용자 수가 1이되면 채팅방 전체 삭제.
            chatMessageRepository.deleteByChatRoomId(chatRoomId);//채팅방 메시지 삭제.
            chatRoomRepository.deleteById(chatRoomId);//todo : 삭제 되는지
            deleteRedis(chatRoomId);
        }
    }

    @Transactional
    @Override//방장만 수정할 수 있도록 설정.
    public void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId) {
        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        Profile profile = queryService.findByProfile(profileId);
        chatRoom.validateChatOwner(profile);

        chatRoom.setName(updateChatRoomDto.getChatRoomName());
        chatRoom.setLimitCount(updateChatRoomDto.getLimitCount());
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        return chattingReader.getMessages(chatRoomId, userId, ChatRoomType.MANY, page);
    }

    private ChatRoomResponseDto toChatRoomsResponseDtoWithRedis(ChatRoom chatRoom, Long userId) {
        Object seqByUser = redisTemplate.opsForHash().get("chatRoomId:" + chatRoom.getId() + ":read", String.valueOf(userId));
        int userSeq = seqByUser == null ? 0 : (Integer) (seqByUser);

        Map<Object, Object> lastMessageInfo = redisTemplate.opsForHash().entries("chat:lastMessageInfo:" + chatRoom.getId());
        String lastMessage = (String) lastMessageInfo.getOrDefault("lastMessage", null);
        String lastMessageTime = (String) lastMessageInfo.getOrDefault("lastMessageTime", null);
        int lastSeq = (Integer) lastMessageInfo.getOrDefault("seq", null);
        int unReadCount = Math.max(lastSeq - userSeq, 0);

        Set<ChatRoomUsersResponseDto> users = chatRoom.getUsers().stream().map(id -> {
                    Profile profile = queryService.findByProfile(id);
                    return ChatRoomUsersResponseDto.builder()
                            .userId(profile.getId())
                            .userImageUrl(profile.getPetImageUrl())
                            .build();
                })//Member일 때도 구현해야할듯.
                .collect(Collectors.toSet());
        return ChatRoomMapper.toChatRoomsResponseDto(chatRoom, userId, lastMessage, unReadCount, users, LocalDateTime.parse(lastMessageTime));
    }
    
    private void deleteRedis(Long chatRoomId) {
        String kInfoOld = "chat:lastMessageInfo:" + chatRoomId;
        String kReadOld = "chatRoomId:" + chatRoomId + ":read";

        redisTemplate.delete(List.of(kInfoOld, kReadOld));
    }

}
