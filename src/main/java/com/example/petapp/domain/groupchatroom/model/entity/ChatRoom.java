package com.example.petapp.domain.groupchatroom.model.entity;

import com.example.petapp.common.base.superclass.BaseEntity;
import com.example.petapp.common.exception.ConflictException;
import com.example.petapp.common.exception.ForbiddenException;
import com.example.petapp.domain.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@SuperBuilder
public class ChatRoom extends BaseEntity {

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int limitCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walking_together_post_id")
    private WalkingTogetherMatch walkingTogetherMatch;

    @Setter
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
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

    public void validateChatOwner(Profile profile) {
        if (!walkingTogetherMatch.getProfile().equals(profile)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public void addUser(Long userId) {
        users.add(userId);
    }

    public void checkUser(Long userId) {
        if (users.contains(userId)) {
            throw new ConflictException("이미 채팅방이있습니다.");
        }
    }
}