package com.example.petapp.domain.chatroom.model;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.chatmessage.model.type.ChatRoomType;
import com.example.petapp.domain.BaseEntity;
import com.example.petapp.domain.member.model.Member;
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

    @ManyToMany
    @JoinTable(
            name = "chat_room_users",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private Set<Member> users = new HashSet<>();

    public void validateUser(Long memberId) {
        if (notContainsMember(memberId)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void validateChatOwner(Profile profile) {
        if (!walkingTogetherPost.getProfile().equals(profile)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "권한이 없습니다.");
        }
    }

    public void deleteUser(Long memberId) {
        users.removeIf(user -> user.getId().equals(memberId));
    }

    public void addUser(Member member) {
        users.add(member);
    }

    public void updateInfo(String name, int limitCount) {
        this.name = name;
        this.limitCount = limitCount;
    }

    public void checkUser(Long memberId) {
        if (!notContainsMember(memberId)) {
            throw new PetCommunityException(ErrorCode.CONFLICT, "이미 채팅방이있습니다.");
        }
    }

    private boolean notContainsMember(Long memberId) {
        return users.stream().noneMatch(user -> user.getId().equals(memberId));
    }
}
