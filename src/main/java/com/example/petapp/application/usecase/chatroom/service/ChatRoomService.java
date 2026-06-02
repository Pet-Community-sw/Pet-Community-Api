package com.example.petapp.application.usecase.chatroom.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.application.usecase.chatmessage.ReaderUseCase;
import com.example.petapp.application.usecase.chatmessage.model.dto.LastMessageInfoDto;
import com.example.petapp.application.usecase.chatmessage.model.type.ChatRoomType;
import com.example.petapp.application.usecase.chatroom.ChatRoomUseCase;
import com.example.petapp.application.usecase.chatroom.dto.request.UpdateChatRoomDto;
import com.example.petapp.application.usecase.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.application.usecase.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.usecase.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.usecase.chatroom.mapper.ChatRoomMapper;
import com.example.petapp.application.usecase.profile.ProfileUseCase;
import com.example.petapp.application.usecase.profile.dto.response.ChatRoomUsersResponseDto;
import com.example.petapp.domain.chatmessage.ChatMessageRepository;
import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService implements ChatRoomUseCase {

    private final ProfileUseCase profileUseCase;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReaderUseCase readerUseCase;
    private final SeqCachePort seqCachePort;
    private final ReadMessageCachePort readMessageCachePort;
    private final LastMessageCachePort lastMessageCachePort;

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomResponseDto> getChatRooms(Long profileId) {//todo : 나중에 One으로도 같이 내보내면 될듯?
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll(profileId, ChatRoomType.MANY);//나중에 타입 파라미터로 방아야함
        if (chatRoomList.isEmpty()) {
            return List.of();
        }

        Set<Long> profileIds = chatRoomList.stream()
                .flatMap(chatRoom -> chatRoom.getUsers().stream())
                .collect(Collectors.toSet());
        Map<Long, Profile> profileMap = profileUseCase.findMapOrThrow(profileIds);

        return chatRoomList.stream()
                .map(chatRoom -> toChatRoomsResponseDtoWithRedis(chatRoom, profileId, profileMap))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto createChatRoom(WalkingTogetherPost walkingTogetherPost, Profile profile) {
        Optional<ChatRoom> chatRoom = find(walkingTogetherPost);
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

    @Transactional
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
    public ChatRoom find(Long id) {
        return chatRoomRepository.find(id).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 채팅방은 없습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ChatRoom> find(WalkingTogetherPost walkingTogetherPost) {
        return chatRoomRepository.find(walkingTogetherPost);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isExist(Long chatRoomId, Long profileId) {
        return chatRoomRepository.existAndContain(chatRoomId, profileId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getUsers(Long chatRoomId) {
        ChatRoom chatRoom = find(chatRoomId);
        return new ArrayList<>(chatRoom
                .getUsers());
    }

    @Transactional
    @Override
    public void deleteChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = find(chatRoomId);
        chatRoom.validateUser(userId);
        chatRoom.deleteUser(userId);
        readMessageCachePort.deleteReadSeq(chatRoomId, userId);
        if (chatRoomRepository.countByProfile(chatRoomId) <= 1) {//방 사용자 수가 1이되면 채팅방 전체 삭제.
            chatMessageRepository.delete(chatRoomId);//채팅방 메시지 삭제.
            chatRoomRepository.delete(chatRoomId);
            deleteRedis(chatRoomId);
        }
    }

    @Transactional
    @Override//방장만 수정할 수 있도록 설정.
    public void updateChatRoom(Long chatRoomId, UpdateChatRoomDto updateChatRoomDto, Long profileId) {
        ChatRoom chatRoom = find(chatRoomId);
        Profile profile = profileUseCase.findOrThrow(profileId);
        chatRoom.validateChatOwner(profile);
        chatRoom.updateInfo(updateChatRoomDto.getChatRoomName(), updateChatRoomDto.getLimitCount());
    }

    @Transactional(readOnly = true)
    @Override//todo : service 따로 둬야할듯.
    public ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page) {
        ChatRoom chatRoom = find(chatRoomId);
        chatRoom.validateUser(userId);
        return readerUseCase.getMessages(chatRoom, userId, page);
    }

    @Transactional(readOnly = true)
    @Override
    public ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long userId) {
        ChatRoom chatRoom = find(chatRoomId);
        chatRoom.validateUser(userId);
        return readerUseCase.getAfterMessages(chatRoom, lastSeq, userId);
    }

    private ChatRoomResponseDto toChatRoomsResponseDtoWithRedis(ChatRoom chatRoom, Long userId, Map<Long, Profile> profileMap) {
        LastMessageInfoDto lastMessageInfoDto = lastMessageCachePort.findLastMessageInfo(chatRoom.getId());
        Set<ChatRoomUsersResponseDto> users = chatRoom.getUsers().stream().map(id ->
                        ChatRoomMapper.toChatRoomUsersResponseDto(profileMap.get(id))
                )//Member일 때도 구현해야할듯.
                .collect(Collectors.toSet());
        return ChatRoomMapper.toChatRoomsResponseDto(chatRoom, userId, lastMessageInfoDto, users);
    }

    private void deleteRedis(Long chatRoomId) {
        seqCachePort.deleteSeq(chatRoomId);
        lastMessageCachePort.deleteLastMessageInfo(chatRoomId);
        readMessageCachePort.deleteRoomReadState(chatRoomId);
    }
}
