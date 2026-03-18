package com.example.petapp.application.service.chatroom;

import com.example.petapp.application.in.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatroom.dto.request.UpdateChatRoomDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.chatroom.mapper.ChatRoomMapper;
import com.example.petapp.application.in.chatting.ReaderUseCase;
import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.application.in.profile.dto.response.ChatRoomUsersResponseDto;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
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
public class ChatRoomService implements ChatRoomUseCase {

    private final ProfileQueryUseCase profileQueryUseCase;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReaderUseCase readerUseCase;
    private final ChatRoomQueryUseCase chatRoomQueryUseCase;
    private final SeqCachePort seqCachePort;
    private final ReadMessageCachePort readMessageCachePort;
    private final LastMessageCachePort lastMessageCachePort;

    @Override
    public List<ChatRoomResponseDto> getChatRooms(Long profileId) {//todo : 나중에 One으로도 같이 내보내면 될듯?
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll(profileId, ChatRoomType.MANY);//나중에 타입 파라미터로 방아야함
        return chatRoomList.stream()
                .map(chatRoom -> toChatRoomsResponseDtoWithRedis(chatRoom, profileId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto createChatRoom(WalkingTogetherPost walkingTogetherPost, Profile profile) {
        Optional<ChatRoom> chatRoom = chatRoomQueryUseCase.find(walkingTogetherPost);
        if (chatRoom.isEmpty()) {//채팅방이 없으면 새로운생성 있으면 profiles에 신청자 Profile 추가
            ChatRoom savedChatRoom = chatRoomRepository.save(ChatRoomMapper.toEntity(walkingTogetherPost, profile));
            return new CreateChatRoomResponseDto(savedChatRoom.getId(), true);
        } else {
            ChatRoom realChatRoom = chatRoom.get();
            realChatRoom.checkUser(profile.getId());
            walkingTogetherPost.checkLimitCount(realChatRoom);
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
        ChatRoom chatRoom = chatRoomQueryUseCase.find(chatRoomId);
        return new ArrayList<>(chatRoom
                .getUsers());
    }

    @Transactional
    @Override
    public void deleteChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomQueryUseCase.find(chatRoomId);
        chatRoom.validateUser(userId);
        chatRoom.deleteUser(userId);
        readMessageCachePort.delete(chatRoomId, userId);
        if (chatRoomRepository.countByProfile(chatRoomId) <= 1) {//방 사용자 수가 1이되면 채팅방 전체 삭제.
            chatMessageRepository.delete(chatRoomId);//채팅방 메시지 삭제.
            chatRoomRepository.delete(chatRoomId);
            deleteRedis(chatRoomId);
        }
    }

    @Transactional
    @Override//방장만 수정할 수 있도록 설정.
    public void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId) {
        ChatRoom chatRoom = chatRoomQueryUseCase.find(chatRoomId);
        Profile profile = profileQueryUseCase.findOrThrow(profileId);
        chatRoom.validateChatOwner(profile);
        chatRoom.setName(updateChatRoomDto.getChatRoomName());
        chatRoom.setLimitCount(updateChatRoomDto.getLimitCount());
    }

    @Transactional(readOnly = true)
    @Override//todo : service 따로 둬야할듯.
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        return readerUseCase.getMessages(chatRoomId, userId, page);
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long userId) {
        return readerUseCase.getAfterMessages(chatRoomId, lastSeq, userId);
    }

    private ChatRoomResponseDto toChatRoomsResponseDtoWithRedis(ChatRoom chatRoom, Long userId) {
        Long userSeq = readMessageCachePort.find(chatRoom.getId(), userId);
        LastMessageInfoDto lastMessageInfoDto = lastMessageCachePort.find(chatRoom.getId());
        long unReadCount = Math.max(lastMessageInfoDto.getLastSeq() - userSeq, 0);
        Set<ChatRoomUsersResponseDto> users = chatRoom.getUsers().stream().map(id ->
                        ChatRoomMapper.toChatRoomUsersResponseDto(profileQueryUseCase.findOrThrow(id))
                )//Member일 때도 구현해야할듯.
                .collect(Collectors.toSet());
        return ChatRoomMapper.toChatRoomsResponseDto(chatRoom, userId, lastMessageInfoDto, unReadCount, users);
    }

    private void deleteRedis(Long chatRoomId) {
        seqCachePort.delete(chatRoomId);
        lastMessageCachePort.delete(chatRoomId);
        readMessageCachePort.delete(chatRoomId);
    }
}
