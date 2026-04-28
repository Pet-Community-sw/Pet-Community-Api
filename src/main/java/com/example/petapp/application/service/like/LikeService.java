package com.example.petapp.application.service.like;

import com.example.petapp.application.in.like.LikeUseCase;
import com.example.petapp.application.in.like.dto.request.LikeCountDto;
import com.example.petapp.application.in.like.dto.response.LikeResponseDto;
import com.example.petapp.application.in.like.mapper.LikeMapper;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.post.PostUseCase;
import com.example.petapp.domain.like.LikeRepository;
import com.example.petapp.domain.like.model.Like;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor//like를 superclass로 둠으로써 likeId 겹칠일이없음. 코드 100줄이상 줄임. ㄷㄷ
public class LikeService implements LikeUseCase {

    private final LikeRepository repository;
    private final MemberUseCase memberUseCase;
    private final PostUseCase<Post> postUseCase;
    private final PostRepository<Post> postRepository;
    private final ApplicationEventPublisher eventPublisher;

    /*
     *  조회 ->분기 ->저장 : 동시성 이슈 발생할 수 있음. 분기 처리중 저장했다면?
     * redis에서 꺼내서 비교하는게 좋을 듯
     * */
    @Transactional
    @Override
    public boolean createAndDelete(Long postId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Post post = postUseCase.findOrThrow(postId);
        Optional<Like> existingLike = post.getLikes().stream().filter(like -> like.getMember().equals(member)).findFirst();

        eventPublisher.publishEvent(new NotificationEvent(post.getMember().getId(), member.getName() + "님이 회원님의 게시물을 좋아합니다."));

        return existingLike.map(like -> delete(like, post)).orElseGet(() -> create(post, member));
    }

    private boolean delete(Like like, Post post) {
        post.removeLikes(like);
        repository.delete(like);
        postRepository.decrementLikeCount(post.getId());
        return false;
    }

    public boolean create(Post post, Member member) {
        Like like = LikeMapper.toEntity(member, post);
        post.countUpLike(like);
        repository.save(like);
        postRepository.incrementLikeCount(post.getId());
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public LikeResponseDto get(Long postId) {
        return LikeMapper.toLikeResponseDto(postUseCase.findOrThrow(postId).getLikes());
    }

    @Transactional(readOnly = true)
    @Override
    public <T extends Post> Map<Long, Long> getCountMap(List<T> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        List<LikeCountDto> likeCountDtos = repository.countByPosts(postIds);//todo : 추후 redis로 변경해야할듯?
        return likeCountDtos.stream().collect(Collectors.toMap(LikeCountDto::getPostId, LikeCountDto::getLikeCount));
    }

    @Transactional(readOnly = true)
    @Override
    public Long countByPost(Post post) {
        return repository.countByPost(post);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean exist(Post post, Member member) {
        return repository.exist(post, member);
    }
}
