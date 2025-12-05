package com.example.petapp.domain.groupchatroom;

import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.chatting.reader.ChattingReader;
import com.example.petapp.domain.groupchatroom.mapper.ChatRoomMapper;
import com.example.petapp.domain.groupchatroom.model.dto.request.UpdateChatRoomDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.ChatRoomResponseDto;
import com.example.petapp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.dto.response.ChatRoomUsersResponseDto;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final QueryService queryService;
    private final InMemoryService inMemoryService;

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomResponseDto> getChatRooms(Long profileId) {//todo : 나중에 One으로도 같이 내보내면 될듯?
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserIdAndChatRoomType(profileId, ChatRoomType.MANY);//나중에 타입 파라미터로 방아야함
        return chatRoomList.stream()
                .map(chatRoom -> toChatRoomsResponseDtoWithRedis(chatRoom, profileId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto createChatRoom(WalkingTogetherMatch walkingTogetherMatch, Profile profile) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByWalkingTogetherMatch(walkingTogetherMatch);
        if (chatRoom.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom savedChatRoom = chatRoomRepository.save(ChatRoomMapper.toEntity(walkingTogetherMatch, profile));
            return new CreateChatRoomResponseDto(savedChatRoom.getId(), true);
        } else {
            ChatRoom realChatRoom = chatRoom.get();
            realChatRoom.checkUser(profile.getId());
            walkingTogetherMatch.checkLimitCount(realChatRoom);
            realChatRoom.addUser(profile.getId());
            return new CreateChatRoomResponseDto(realChatRoom.getId(), false);
        }
    }

    //todo : type잘봐보자 type별로 할건지 다시생각
    @Override
    public CreateChatRoomResponseDto createChatRoom(Member member, Member applicationMember) {
        ChatRoom chatRoom = ChatRoomMapper.toEntity(member);
        chatRoom.addUser(member.getId());
        chatRoom.addUser(applicationMember.getId());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return new CreateChatRoomResponseDto(savedChatRoom.getId(), false);
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
        inMemoryService.deleteReadData(chatRoomId, userId);
        if (chatRoomRepository.countByProfile(chatRoomId) <= 1) {//방 사용자 수가 1이되면 채팅방 전체 삭제.
            chatMessageRepository.deleteByChatRoomId(chatRoomId);//채팅방 메시지 삭제.
            chatRoomRepository.deleteById(chatRoomId);
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
        return chattingReader.getMessages(chatRoomId, userId, page);
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long userId) {
        return chattingReader.getAfterMessages(chatRoomId, lastSeq, userId);
    }

    private ChatRoomResponseDto toChatRoomsResponseDtoWithRedis(ChatRoom chatRoom, Long userId) {
        Long userSeq = inMemoryService.getReadData(chatRoom.getId(), userId);
        LastMessageInfoDto lastMessageInfoDto = inMemoryService.getLastMessageInfoData(chatRoom.getId());
        long unReadCount = Math.max(lastMessageInfoDto.getLastSeq() - userSeq, 0);
        Set<ChatRoomUsersResponseDto> users = chatRoom.getUsers().stream().map(id ->
                        ChatRoomMapper.toChatRoomUsersResponseDto(queryService.findByProfile(id))
                )//Member일 때도 구현해야할듯.
                .collect(Collectors.toSet());
        return ChatRoomMapper.toChatRoomsResponseDto(chatRoom, userId, lastMessageInfoDto, unReadCount, users);
    }

    private void deleteRedis(Long chatRoomId) {
        inMemoryService.deleteRoomSeq(chatRoomId);
        inMemoryService.deleteLastMessageInfoData(chatRoomId);
        inMemoryService.deleteReadData(chatRoomId);
    }
}
