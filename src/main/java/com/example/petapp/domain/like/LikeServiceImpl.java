package com.example.petapp.domain.like;

import com.example.petapp.common.base.util.notification.SendNotificationUtil;
import com.example.petapp.domain.like.mapper.LikeMapper;
import com.example.petapp.domain.like.model.dto.request.LikeCountDto;
import com.example.petapp.domain.like.model.dto.response.LikeResponseDto;
import com.example.petapp.domain.like.model.entity.Like;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.common.Post;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor//like를 superclass로 둠으로써 likeId 겹칠일이없음. 코드 100줄이상 줄임. ㄷㄷ
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final SendNotificationUtil sendNotificationUtil;
    private final QueryService queryService;
    private final InMemoryService inMemoryService;


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
        return existingLike.map(like -> deleteLike(like, post)).orElseGet(() -> createLike(post, member));
    }

    private boolean deleteLike(Like like, Post post) {
        post.removeLikes(like);
        likeRepository.delete(like);
        log.info("좋아요 삭제");
        inMemoryService.deleteLikeData(like.getMember().getId(), like.getPost().getId());
        return false;
    }

    private boolean createLike(Post post, Member member) {
        log.info("좋아요 생성");
        Like like = LikeMapper.toEntity(member, post);
        post.countUpLike(like);
        likeRepository.save(like);
        inMemoryService.createLikeData(member.getId(), post.getId());

        sendNotification(post, member);

        return true;
    }

    private void sendNotification(Post post, Member member) {
        String message = member.getName() + "님이 회원님의 게시물을 좋아합니다.";
        sendNotificationUtil.sendNotification(post.getMember(), message);
    }
}
