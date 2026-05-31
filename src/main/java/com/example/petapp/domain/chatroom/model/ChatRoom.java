package com.example.petapp.domain.chatroom.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.chatmessage.model.type.ChatRoomType;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@SuperBuilder
public class ChatRoom extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    @NotNull
    @Column(nullable = false)
    private int limitCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walking_together_post_id")
    private WalkingTogetherPost walkingTogetherPost;

    @ElementCollection
    @CollectionTable(
            name = "chat_room_users",
            joinColumns = @JoinColumn(name = "chat_room_id")
    )
    @Column(name = "user_id")
    @Builder.Default
    private Set<Long> users = new HashSet<>();//memberchatroom을 삭제 시 Long으로 변환 해야할듯, profileId 와 memberId가 같을 수 있음. type설정해야하나?uuid로 한다면?

    public void validateUser(Long userId) {
        if (!users.contains(userId)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void validateChatOwner(Profile profile) {
        if (!walkingTogetherPost.getProfile().equals(profile)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public void addUser(Long userId) {
        users.add(userId);
    }

    public void updateInfo(String name, int limitCount) {
        this.name = name;
        this.limitCount = limitCount;
    }

    public void checkUser(Long userId) {
        if (users.contains(userId)) {
            throw new PetCommunityException(ErrorCode.CONFLICT, "이미 채팅방이있습니다.");
        }
    }
}
