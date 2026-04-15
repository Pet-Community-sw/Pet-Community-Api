package com.example.petapp.application.service.chatroom;

import com.example.petapp.application.in.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.application.in.chatroom.dto.request.UpdateChatRoomDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.application.in.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.chatroom.dto.request.ChatMessageDtoMember;
import com.example.petapp.application.in.chatting.ReaderUseCase;
import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ProfileQueryUseCase profileQueryUseCase;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ReaderUseCase readerUseCase;
    @Mock
    private ChatRoomQueryUseCase chatRoomQueryUseCase;
    @Mock
    private SeqCachePort seqCachePort;
    @Mock
    private ReadMessageCachePort readMessageCachePort;
    @Mock
    private LastMessageCachePort lastMessageCachePort;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void 모임게시글_채팅방이_없으면_새로_생성한다() {
        WalkingTogetherPost post = org.mockito.Mockito.mock(WalkingTogetherPost.class);
        Profile owner = org.mockito.Mockito.mock(Profile.class);
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        ChatRoom saved = org.mockito.Mockito.mock(ChatRoom.class);
        when(saved.getId()).thenReturn(1L);
        when(post.getProfile()).thenReturn(owner);
        when(owner.getPetName()).thenReturn("몽이");
        when(owner.getId()).thenReturn(10L);
        when(post.getLimitCount()).thenReturn(4);
        when(profile.getId()).thenReturn(20L);
        when(chatRoomQueryUseCase.find(post)).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(saved);

        CreateChatRoomResponseDto result = chatRoomService.createChatRoom(post, profile);

        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.isCreated()).isTrue();
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    void 모임게시글_채팅방이_있으면_참여자만_추가한다() {
        WalkingTogetherPost post = org.mockito.Mockito.mock(WalkingTogetherPost.class);
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(profile.getId()).thenReturn(20L);
        when(chatRoom.getId()).thenReturn(7L);
        when(chatRoomQueryUseCase.find(post)).thenReturn(Optional.of(chatRoom));

        CreateChatRoomResponseDto result = chatRoomService.createChatRoom(post, profile);

        assertThat(result.getChatRoomId()).isEqualTo(7L);
        assertThat(result.isCreated()).isFalse();
        verify(chatRoom).checkUser(20L);
        verify(post).checkLimitCount(chatRoom);
        verify(chatRoom).addUser(20L);
    }

    @Test
    void 일대일_채팅방을_생성한다() {
        Member member = org.mockito.Mockito.mock(Member.class);
        Member applicationMember = org.mockito.Mockito.mock(Member.class);
        ChatRoom saved = org.mockito.Mockito.mock(ChatRoom.class);
        when(member.getId()).thenReturn(1L);
        when(applicationMember.getId()).thenReturn(2L);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(saved);
        when(saved.getId()).thenReturn(9L);

        CreateChatRoomResponseDto result = chatRoomService.createChatRoom(member, applicationMember);

        assertThat(result.getChatRoomId()).isEqualTo(9L);
        assertThat(result.isCreated()).isFalse();
    }

    @Test
    void 마지막_참여자_퇴장시_채팅방과_캐시를_정리한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoomQueryUseCase.find(1L)).thenReturn(chatRoom);
        when(chatRoomRepository.countByProfile(1L)).thenReturn(1);

        chatRoomService.deleteChatRoom(1L, 100L);

        verify(chatRoom).validateUser(100L);
        verify(chatRoom).deleteUser(100L);
        verify(readMessageCachePort).delete(1L, 100L);
        verify(chatMessageRepository).delete(1L);
        verify(chatRoomRepository).delete(1L);
        verify(seqCachePort).delete(1L);
        verify(lastMessageCachePort).delete(1L);
        verify(readMessageCachePort).delete(1L);
    }

    @Test
    void 참여자가_남아있으면_채팅방_삭제는_하지_않는다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoomQueryUseCase.find(1L)).thenReturn(chatRoom);
        when(chatRoomRepository.countByProfile(1L)).thenReturn(2);

        chatRoomService.deleteChatRoom(1L, 100L);

        verify(chatRoom).validateUser(100L);
        verify(chatRoom).deleteUser(100L);
        verify(readMessageCachePort).delete(1L, 100L);
        verify(chatMessageRepository, never()).delete(1L);
        verify(chatRoomRepository, never()).delete(1L);
        verify(seqCachePort, never()).delete(1L);
        verify(lastMessageCachePort, never()).delete(1L);
        verify(readMessageCachePort, never()).delete(1L);
    }

    @Test
    void 채팅방_수정시_방장검증후_이름과_정원을_변경한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        UpdateChatRoomDto dto = new UpdateChatRoomDto("새로운 방", 8);
        when(chatRoomQueryUseCase.find(1L)).thenReturn(chatRoom);
        when(profileQueryUseCase.findOrThrow(10L)).thenReturn(profile);

        chatRoomService.updateChatRoom(1L, dto, 10L);

        verify(chatRoom).validateChatOwner(profile);
        verify(chatRoom).setName("새로운 방");
        verify(chatRoom).setLimitCount(8);
    }

    @Test
    void 채팅내역_조회는_readerUseCase에_위임한다() {
        ChatMessageResponseDto expected = new ChatMessageResponseDto(
                1L,
                List.of(ChatMessageDtoMember.builder().message("m").build())
        );
        when(readerUseCase.getMessages(1L, 2L, 0)).thenReturn(expected);

        ChatMessageResponseDto result = chatRoomService.getMessages(1L, 2L, 0);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void 이후메시지_조회는_readerUseCase에_위임한다() {
        ChatMessageResponseDto expected = new ChatMessageResponseDto(
                1L,
                List.of(ChatMessageDtoMember.builder().message("m").build())
        );
        when(readerUseCase.getAfterMessages(1L, 5L, 2L)).thenReturn(expected);

        ChatMessageResponseDto result = chatRoomService.getAfterMessages(1L, 5L, 2L);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void 채팅방_목록조회시_안읽은수와_사용자정보를_매핑한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        WalkingTogetherPost walkingTogetherPost = org.mockito.Mockito.mock(WalkingTogetherPost.class);
        Profile owner = org.mockito.Mockito.mock(Profile.class);
        Profile p1 = org.mockito.Mockito.mock(Profile.class);
        Profile p2 = org.mockito.Mockito.mock(Profile.class);

        when(chatRoomRepository.findAll(1L, ChatRoomType.MANY)).thenReturn(List.of(chatRoom));
        when(chatRoom.getId()).thenReturn(100L);
        when(chatRoom.getName()).thenReturn("테스트 채팅방");
        when(chatRoom.getUsers()).thenReturn(Set.of(1L, 2L));
        when(chatRoom.getWalkingTogetherPost()).thenReturn(walkingTogetherPost);
        when(walkingTogetherPost.getProfile()).thenReturn(owner);
        when(owner.getId()).thenReturn(1L);

        when(readMessageCachePort.find(100L, 1L)).thenReturn(2L);
        when(lastMessageCachePort.find(100L)).thenReturn(
                LastMessageInfoDto.builder()
                        .lastSeq(5L)
                        .lastMessage("마지막 메시지")
                        .lastMessageTime("2026-01-01T00:00:00")
                        .build()
        );

        when(profileQueryUseCase.findMapOrThrow(Set.of(1L, 2L))).thenReturn(Map.of(1L, p1, 2L, p2));
        when(p1.getId()).thenReturn(1L);
        when(p1.getPetImageUrl()).thenReturn("1.png");
        when(p2.getId()).thenReturn(2L);
        when(p2.getPetImageUrl()).thenReturn("2.png");

        List<ChatRoomResponseDto> result = chatRoomService.getChatRooms(1L);

        assertThat(result).hasSize(1);
        ChatRoomResponseDto dto = result.get(0);
        assertThat(dto.getChatRoomId()).isEqualTo(100L);
        assertThat(dto.getUnReadCount()).isEqualTo(3L);
        assertThat(dto.isOwner()).isTrue();
        assertThat(dto.getUsers()).hasSize(2);
        verify(profileQueryUseCase).findMapOrThrow(Set.of(1L, 2L));
        verify(profileQueryUseCase, never()).findOrThrow(1L);
        verify(profileQueryUseCase, never()).findOrThrow(2L);
    }

    @Test
    void 채팅방_참여자목록을_반환한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoomQueryUseCase.find(1L)).thenReturn(chatRoom);
        when(chatRoom.getUsers()).thenReturn(Set.of(10L, 20L));

        List<Long> result = chatRoomService.getUsers(1L);

        assertThat(result).containsExactlyInAnyOrder(10L, 20L);
    }
}
