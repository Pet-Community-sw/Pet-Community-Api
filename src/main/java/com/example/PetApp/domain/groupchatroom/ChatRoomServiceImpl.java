package com.example.PetApp.domain.groupchatroom;

import com.example.PetApp.domain.groupchatroom.model.dto.request.UpdateChatRoomDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatRoomsResponseDto;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.groupchatroom.mapper.ChatRoomMapper;
import com.example.PetApp.domain.chatting.ChatMessageRepository;
import com.example.PetApp.domain.chatting.ChattingReader;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.PetApp.domain.chatting.model.entity.ChatMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChattingReader chattingReader;
    private final StringRedisTemplate redisTemplate;
    private final QueryService queryService;

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomsResponseDto> getChatRooms(Long profileId) {
        Profile profile = queryService.findByProfile(profileId);
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByProfilesContains(profile);
        return chatRoomList.stream()
                .map(chatRoom -> toChatRoomsResponseDtoWithRedis(chatRoom, profile.getId()))
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public CreateChatRoomResponseDto createChatRoom(WalkingTogetherMatch walkingTogetherMatch, Profile profile) {
        Optional<ChatRoom> chatRoom1 = chatRoomRepository.findByWalkingTogetherPost(walkingTogetherMatch);
        if (chatRoom1.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom chatRoom = ChatRoomMapper.toEntity(walkingTogetherMatch, profile);
            ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
            return new CreateChatRoomResponseDto(savedChatRoom.getId(), true);
        } else {
            if (walkingTogetherMatch.getLimitCount() <= chatRoom1.get().getProfiles().size()) {
                throw new ConflictException("인원초과");//채팅방 limitCount설정.
            }
            ChatRoom chatRoom = chatRoom1.get();
            chatRoom.addProfiles(profile);
            return new CreateChatRoomResponseDto(chatRoom.getId(), false);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getProfiles(Long chatRoomId) {
        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);

        return chatRoom
                .getProfiles()
                .stream()
                .map(Profile::getId)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteChatRoom(Long chatRoomId, Long profileId) {
        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        Profile profile = queryService.findByProfile(profileId);
        List<Profile> profiles = chatRoom.getProfiles();
        if (!(profiles.contains(profile))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        profiles.remove(profile);
        chatRoom.setProfiles(profiles);//방 사용자 수가 1이되면 채팅방 전체 삭제.
        if (chatRoomRepository.countByProfile(chatRoomId) <= 1) {
            chatMessageRepository.deleteByChatRoomId(chatRoomId);//채팅방 메시지 삭제.
            chatRoomRepository.deleteById(chatRoomId);//이게 왜안되는교?
        }
    }

    @Transactional
    @Override//방장만 수정할 수 있도록 설정.
    public void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId) {
        ChatRoom chatRoom = queryService.findByChatRoom(chatRoomId);
        Profile profile = queryService.findByProfile(profileId);
        if (!(chatRoom.getWalkingTogetherMatch().getProfile().getId().equals(profile.getId()))) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        chatRoom.setName(updateChatRoomDto.getChatRoomName());
        chatRoom.setLimitCount(updateChatRoomDto.getLimitCount());
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        return chattingReader.getMessages(chatRoomId, userId, ChatRoomType.MANY, page);
    }

    private ChatRoomsResponseDto toChatRoomsResponseDtoWithRedis(ChatRoom chatRoom, Long profileId) {
        String lastMessage = redisTemplate.opsForValue().get("chat:lastMessage" + chatRoom.getId());
        String lastMessageTime = redisTemplate.opsForValue().get("chat:lastMessageTime" + chatRoom.getId());
        String count = redisTemplate.opsForValue().get("unRead:" + chatRoom.getId() + ":" + profileId);
        int unReadCount = count != null ? Integer.parseInt(count) : 0;
        LocalDateTime lastMessageLocalDateTime = null;
        if (lastMessageTime != null) {
            lastMessageLocalDateTime = LocalDateTime.parse(lastMessageTime);
        }
        return ChatRoomMapper.toChatRoomsResponseDto(chatRoom, profileId, lastMessage, unReadCount, lastMessageLocalDateTime);
    }
}
