package com.example.PetApp.domain.chatting.offline;

import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfflineUserServiceImpl implements OfflineUserService{

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, ChatRoom chatRoom) {
        List<Profile> profiles = chatRoom.getProfiles();
        Set<String> onlineProfiles = stringRedisTemplate.opsForSet()
                .members("chatRoomId:" + chatRoom.getId() + ":onlineProfiles");
        List<Long> offlineProfiles = profiles.stream()
                .map(Profile::getId)
                .filter(profileId -> onlineProfiles == null || !onlineProfiles.contains(profileId.toString()))
                .collect(Collectors.toList());

        chatMessage.setUsers(offlineProfiles);
        chatMessage.setChatUnReadCount(offlineProfiles.size());

        log.info("메시지에 대한 안 읽은 유저 추적 : {}", offlineProfiles);
    }

    @Override
    public void setOfflineUsersAndUnreadCount(ChatMessage chatMessage, MemberChatRoom memberChatRoom) {
        List<Member> members = memberChatRoom.getMembers();
        Set<String> onlineMembers = stringRedisTemplate.opsForSet()
                .members("memberChatRoomId:" + memberChatRoom.getId() + ":onlineMembers");
        List<Long> offlineMembers = members.stream()
                .map(Member::getId)
                .filter(memberId -> onlineMembers == null || !onlineMembers.contains(memberId.toString()))
                .collect(Collectors.toList());

        chatMessage.setUsers(offlineMembers);
        chatMessage.setChatUnReadCount(offlineMembers.size());

        log.info("메시지에 대한 안 읽은 유저 추적 : {}", offlineMembers);
    }
}
