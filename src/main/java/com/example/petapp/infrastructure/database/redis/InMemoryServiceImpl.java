package com.example.petapp.infrastructure.database.redis;

import com.example.petapp.domain.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.notification.model.dto.NotificationListDto;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryServiceImpl implements InMemoryService {

    private final RedisTemplate<String, NotificationListDto> notificationRedisTemplate;
    private final RedisTemplate<String, Long> likeRedisTemplate;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void createStringDataWithDuration(String key, String value, long duration) {
        Duration expire = Duration.ofSeconds(duration);
        redisTemplate.opsForValue().set(key, value, expire);
    }

    @Override
    public void createStringData(String key, String memberId) {
        redisTemplate.opsForSet().add(key, memberId);
    }

    @Override
    public String getStringData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean existStringData(String key) {
        if (key == null) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    @Override
    public void deleteStringData(String key) {
        redisTemplate.delete(key);
    }

    //-------------------------------------------------------------------------------------

    @Override
    public Set<Long> getLikeData(Long key) {
        return likeRedisTemplate.opsForSet().members(RedisKeys.postLikes(key));
    }

    @Override
    public void createLikeData(Long key, Long value) {
        likeRedisTemplate.opsForSet().add(RedisKeys.postLikes(key), value);
    }

    @Override
    public void deleteLikeData(Long key, Long value) {
        likeRedisTemplate.opsForSet().remove(RedisKeys.postLikes(key), value);
    }

    //-------------------------------------------------------------------------------------

    @Override
    public void createNotificationData(Long memberId, NotificationListDto notificationListDto, int day) {
        String key = RedisKeys.notifications(memberId);
        notificationRedisTemplate.opsForList().rightPush(key, notificationListDto);
        notificationRedisTemplate.expire(key, Duration.ofDays(day));
    }

    @Override
    public List<NotificationListDto> getNotifications(Long memberId) {
        String key = RedisKeys.notifications(memberId);
        return notificationRedisTemplate.opsForList().range(key, 0, -1);
    }

    //-------------------------------------------------------------------------------------

    @Override
    public void createLocationData(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public String getLocationData(Long id) {
        return redisTemplate.opsForList().index(RedisKeys.walkPath(id), -1);
    }

    @Override
    public List<String> getLocationDatas(Long id) {
        return redisTemplate.opsForList().range(RedisKeys.walkPath(id), 0, -1);
    }

    @Override
    public void deleteLocationData(Long id) {
        redisTemplate.delete(RedisKeys.walkPath(id));
    }

    //-------------------------------------------------------------------------------------

    @Override
    public void createOnlineData(Long chatroomId, Long profileId) {
        redisTemplate.opsForSet().add(RedisKeys.onlineUsers(chatroomId), profileId.toString());
    }

    @Override
    public Set<String> getOnlineDatas(Long id) {
        return Optional.ofNullable(redisTemplate.opsForSet().members(RedisKeys.onlineUsers(id)))
                .orElse(Collections.emptySet());
    }

    //-------------------------------------------------------------------------------------

    @Override
    public void createForeGroundData(Long id) {
        redisTemplate.opsForSet().add(RedisKeys.foregroundMembers(), id.toString());
    }

    @Override
    public Boolean existForeGroundData(Long id) {
        return redisTemplate.opsForSet().isMember(RedisKeys.foregroundMembers(), id.toString());
    }

    //-------------------------------------------------------------------------------------

    @Override
    public void createReadData(ChatMessage chatMessage) {
        redisTemplate.opsForHash().put(
                RedisKeys.readHash(chatMessage.getChatRoomId()),
                String.valueOf(chatMessage.getSenderId()),
                String.valueOf(chatMessage.getSeq())
        );
    }

    @Override
    public void deleteReadData(Long chatRoomId, Long userId) {
        redisTemplate.opsForHash().delete(RedisKeys.readHash(chatRoomId), String.valueOf(userId));
    }

    @Override
    public int getReadData(Long chatRoomId, Long userId) {
        Object seq = redisTemplate.opsForHash().get(RedisKeys.readHash(chatRoomId), String.valueOf(userId));
        return seq == null ? 0 : (Integer) seq;
    }

    @Override
    public void deleteReadData(Long chatRoomId) {
        redisTemplate.delete(RedisKeys.readHash(chatRoomId));
    }

    //-------------------------------------------------------------------------------------

    @Override
    public void createLastMessageInfoData(ChatMessage chatMessage) {
        Map<String, String> lastMessageInfo = new HashMap<>();
        lastMessageInfo.put("seq", String.valueOf(chatMessage.getSeq()));
        lastMessageInfo.put("lastMessage", chatMessage.getMessage());
        lastMessageInfo.put("lastMessageTime", String.valueOf(chatMessage.getMessageTime()));

        redisTemplate.opsForHash().putAll(RedisKeys.lastMessageInfo(chatMessage.getChatRoomId()), lastMessageInfo);
    }

    @Override
    public LastMessageInfoDto getLastMessageInfoData(Long id) {
        Map<Object, Object> lastMessageInfo = redisTemplate.opsForHash().entries(RedisKeys.lastMessageInfo(id));
        String lastMessage = (String) lastMessageInfo.getOrDefault("lastMessage", "");
        String lastMessageTime = (String) lastMessageInfo.getOrDefault("lastMessageTime", "");
        int lastSeq = (Integer) lastMessageInfo.getOrDefault("seq", 0);

        return LastMessageInfoDto.builder()
                .lastSeq(lastSeq)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .build();
    }

    @Override
    public void deleteLastMessageInfoData(Long chatRoomId) {
        redisTemplate.delete(RedisKeys.lastMessageInfo(chatRoomId));
    }
}
