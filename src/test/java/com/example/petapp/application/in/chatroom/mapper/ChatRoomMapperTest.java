package com.example.petapp.application.in.chatroom.mapper;

import com.example.petapp.application.in.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.application.in.profile.dto.response.ChatRoomUsersResponseDto;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatRoomMapperTest {

    @Test
    void 모임채팅방_엔티티를_생성한다() {
        WalkingTogetherPost walkingTogetherPost = mock(WalkingTogetherPost.class);
        Profile ownerProfile = mock(Profile.class);
        Profile applicantProfile = mock(Profile.class);

        when(walkingTogetherPost.getProfile()).thenReturn(ownerProfile);
        when(ownerProfile.getPetName()).thenReturn("몽이");
        when(ownerProfile.getId()).thenReturn(1L);
        when(walkingTogetherPost.getLimitCount()).thenReturn(5);
        when(applicantProfile.getId()).thenReturn(2L);

        ChatRoom result = ChatRoomMapper.toEntity(walkingTogetherPost, applicantProfile);

        assertThat(result.getName()).isEqualTo("몽이님의 방");
        assertThat(result.getChatRoomType()).isEqualTo(ChatRoomType.MANY);
        assertThat(result.getLimitCount()).isEqualTo(5);
        assertThat(result.getUsers()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result.getWalkingTogetherPost()).isSameAs(walkingTogetherPost);
    }

    @Test
    void 일대일채팅방_엔티티를_생성한다() {
        Member member = mock(Member.class);
        when(member.getName()).thenReturn("철수");

        ChatRoom result = ChatRoomMapper.toEntity(member);

        assertThat(result.getChatRoomType()).isEqualTo(ChatRoomType.ONE);
        assertThat(result.getName()).isEqualTo("철수님의 방");
        assertThat(result.getLimitCount()).isEqualTo(2);
    }

    @Test
    void 프로필을_채팅방유저_dto로_변환한다() {
        Profile profile = mock(Profile.class);
        when(profile.getId()).thenReturn(3L);
        when(profile.getPetImageUrl()).thenReturn("pet.png");

        ChatRoomUsersResponseDto result = ChatRoomMapper.toChatRoomUsersResponseDto(profile);

        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getUserImageUrl()).isEqualTo("pet.png");
    }

    @Test
    void 마지막메시지시간이_없으면_null로_변환한다() {
        ChatRoom chatRoom = mock(ChatRoom.class);
        WalkingTogetherPost post = mock(WalkingTogetherPost.class);
        Profile profile = mock(Profile.class);

        when(chatRoom.getId()).thenReturn(1L);
        when(chatRoom.getName()).thenReturn("채팅방");
        when(chatRoom.getUsers()).thenReturn(Set.of(10L, 20L));
        when(chatRoom.getWalkingTogetherPost()).thenReturn(post);
        when(post.getProfile()).thenReturn(profile);
        when(profile.getId()).thenReturn(10L);

        LastMessageInfoDto lastMessageInfoDto = LastMessageInfoDto.builder()
                .lastSeq(5L)
                .lastMessage("마지막")
                .lastMessageTime("")
                .build();

        ChatRoomResponseDto result = ChatRoomMapper.toChatRoomsResponseDto(
                chatRoom,
                10L,
                lastMessageInfoDto,
                2L,
                Set.of(ChatRoomUsersResponseDto.builder().userId(10L).userImageUrl("a.png").build())
        );

        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getChatName()).isEqualTo("채팅방");
        assertThat(result.getUserSize()).isEqualTo(2);
        assertThat(result.getLastMessage()).isEqualTo("마지막");
        assertThat(result.getLastMessageTime()).isNull();
        assertThat(result.isOwner()).isTrue();
    }
}
