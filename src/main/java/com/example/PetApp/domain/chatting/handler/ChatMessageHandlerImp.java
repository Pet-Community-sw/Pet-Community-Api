package com.example.PetApp.domain.chatting.handler;

import com.example.PetApp.config.redis.RedisPublisher;
import com.example.PetApp.domain.chatting.model.entity.ChatMessage;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.memberchatRoom.MemberChatRoomRepository;
import com.example.PetApp.domain.profile.ProfileRepository;
import com.example.PetApp.domain.chatting.ChatRedisCleaner;
import com.example.PetApp.domain.groupchatroom.ChatRoomService;
import com.example.PetApp.common.util.notification.SendNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatMessageHandlerImp implements ChatMessageHandler{

    private final RedisPublisher redisPublisher;
    private final ChatRedisCleaner chatRedisCleaner;
    private final ProfileRepository profileRepository;
    private final ChatRoomService chatRoomService;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final StringRedisTemplate redisTemplate;
    private final SendNotificationUtil sendNotificationUtil;



    @Override
    public void handleEnterMessage(ChatMessage chatMessage) {
        chatMessage.setMessage(chatMessage.getSenderName() + "님이 입장하셨습니다.");
        redisPublisher.publish(chatMessage);
    }

    @Override
    public void handleLeaveMessage(ChatMessage chatMessage, Long senderId) {
        chatMessage.setMessage(chatMessage.getSenderName() + "님이 나가셨습니다.");
        redisPublisher.publish(chatMessage);

        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            chatRedisCleaner.cleanChatRedis(chatMessage, senderId);
            chatRoomService.deleteChatRoom(chatMessage.getChatRoomId(), senderId);
        } else {
            chatRedisCleaner.cleanMemberChatRedis(chatMessage, senderId);
            memberChatRoomRepository.deleteById(chatMessage.getChatRoomId());
        }
    }

    @Override
    public void handleTalkMessage(ChatMessage chatMessage) {
        redisPublisher.publish(chatMessage);
        sendChatNotification(chatMessage);
    }

    private void sendChatNotification(ChatMessage chatMessage) {
        Long chatRoomId = chatMessage.getChatRoomId();
        Long senderId = chatMessage.getSenderId();
        String message = chatMessage.getSenderName() + "님이 메시지를 보냈습니다.";

        if (chatMessage.getChatRoomType() == ChatMessage.ChatRoomType.MANY) {
            List<Long> profileIds = chatRoomService.getProfiles(chatRoomId);
            Set<String> onlineProfiles = redisTemplate.opsForSet()
                    .members("chatRoomId:" + chatRoomId + ":onlineProfiles");

            for (Long profileId : profileIds) {
                if (!profileId.equals(senderId) &&
                        (onlineProfiles == null || !onlineProfiles.contains(profileId.toString()))) {
                    Profile profile = profileRepository.findById(profileId).get();
                    sendNotificationUtil.sendNotification(profile.getMember(), message);
                }
            }
        } else {
            MemberChatRoom chatRoom = memberChatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
            Set<String> onlineMembers = redisTemplate.opsForSet()
                    .members("memberChatRoomId:" + chatRoomId + ":onlineMembers");

            for (Member member : chatRoom.getMembers()) {
                if (!member.getId().equals(senderId) &&
                        (onlineMembers == null || !onlineMembers.contains(member.getId().toString()))) {
                    sendNotificationUtil.sendNotification(member, message);
                }
            }
        }
    }
}
