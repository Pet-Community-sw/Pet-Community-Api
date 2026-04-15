package com.example.petapp.domain.chatroom.model;

import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import com.example.petapp.interfaces.exception.ConflictException;
import com.example.petapp.interfaces.exception.ForbiddenException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatRoomTest {

    @Test
    void 참여하지_않은_유저면_validateUser에서_예외가_발생한다() {
        ChatRoom chatRoom = ChatRoom.builder()
                .name("테스트 방")
                .chatRoomType(ChatRoomType.MANY)
                .limitCount(3)
                .users(new HashSet<>(Set.of(1L)))
                .build();

        assertThatThrownBy(() -> chatRoom.validateUser(2L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    void 방장이_아닌_프로필이면_validateChatOwner에서_예외가_발생한다() {
        Profile owner = mock(Profile.class);
        Profile other = mock(Profile.class);
        WalkingTogetherPost walkingTogetherPost = mock(WalkingTogetherPost.class);
        when(walkingTogetherPost.getProfile()).thenReturn(owner);

        ChatRoom chatRoom = ChatRoom.builder()
                .name("테스트 방")
                .chatRoomType(ChatRoomType.MANY)
                .limitCount(3)
                .walkingTogetherPost(walkingTogetherPost)
                .users(new HashSet<>(Set.of(1L)))
                .build();

        assertThatThrownBy(() -> chatRoom.validateChatOwner(other))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    void addUser와_deleteUser는_참여자목록을_수정한다() {
        ChatRoom chatRoom = ChatRoom.builder()
                .name("테스트 방")
                .chatRoomType(ChatRoomType.MANY)
                .limitCount(3)
                .users(new HashSet<>())
                .build();

        chatRoom.addUser(1L);
        chatRoom.addUser(2L);
        chatRoom.deleteUser(1L);

        assertThat(chatRoom.getUsers()).containsExactly(2L);
    }

    @Test
    void 이미_참여중인_유저면_checkUser에서_예외가_발생한다() {
        ChatRoom chatRoom = ChatRoom.builder()
                .name("테스트 방")
                .chatRoomType(ChatRoomType.MANY)
                .limitCount(3)
                .users(new HashSet<>(Set.of(1L)))
                .build();

        assertThatThrownBy(() -> chatRoom.checkUser(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 채팅방이있습니다.");
    }
}
