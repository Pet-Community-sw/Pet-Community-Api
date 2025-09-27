package com.example.PetApp.domain.like;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.like.model.entity.Like;
import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.like.model.dto.request.LikeCountDto;
import com.example.PetApp.domain.like.model.dto.response.LikeResponseDto;
import com.example.PetApp.domain.like.mapper.LikeMapper;
import com.example.PetApp.domain.query.QueryService;
import com.example.PetApp.common.util.notification.SendNotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor//like를 superclass로 둠으로써 likeId 겹칠일이없음. 코드 100줄이상 줄임. ㄷㄷ
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final SendNotificationUtil sendNotificationUtil;
    private final RedisTemplate<String, Long> likeRedisTemplate;
    private final QueryService queryService;


    @Transactional(readOnly = true)
    @Override
    public LikeResponseDto getLikes(Long postId) {
        return LikeMapper.toLikeResponseDto(queryService.findByPost(postId).getLikes());
    }

    @Transactional(readOnly = true)
    @Override
    public <T extends Post> Map<Long, Long> getLikeCountMap(List<T> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        List<LikeCountDto> likeCountDtos = likeRepository.countByPostIds(postIds);
        return likeCountDtos.stream().collect(Collectors.toMap(LikeCountDto::getPostId, LikeCountDto::getLikeCount));
    }

    @Transactional
    @Override
    public boolean createAndDeleteLike(Long postId, String email) {
        Member member = queryService.findByMember(email);
        Post post = queryService.findByPost(postId);
        Optional<Like> existingLike = post.getLikes().stream().filter(like -> like.getMember().equals(member)).findFirst();
        return existingLike.map(this::deleteLike).orElseGet(() -> createLike(post, member));
    }

    private boolean deleteLike(Like like) {
        log.info("좋아요 삭제");
        likeRepository.delete(like);
        likeRedisTemplate.opsForSet().remove("post:likes:" + like.getMember().getId(), like.getPost().getId());
        return false;
    }

    private boolean createLike(Post post, Member member) {
        log.info("좋아요 생성");
        Like like = LikeMapper.toEntity(member, post);
        post.countUpLike(like);
        likeRepository.save(like);
        likeRedisTemplate.opsForSet().add("post:likes:" + member.getId(), post.getId());

        sendNotification(post, member);

        return true;
    }

    private void sendNotification(Post post, Member member) {
        String message = member.getName() + "님이 회원님의 게시물을 좋아합니다.";
        sendNotificationUtil.sendNotification(post.getMember(), message);
    }
}
