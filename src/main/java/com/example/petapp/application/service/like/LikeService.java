package com.example.petapp.application.service.like;

import com.example.petapp.application.in.like.LikeUseCase;
import com.example.petapp.application.in.like.mapper.LikeMapper;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.post.PostQueryUseCase;
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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor//like를 superclass로 둠으로써 likeId 겹칠일이없음. 코드 100줄이상 줄임. ㄷㄷ
public class LikeService implements LikeUseCase {

    private final LikeRepository repository;
    private final MemberQueryUseCase memberQueryUseCase;
    private final PostQueryUseCase<Post> postQueryUseCase;
    private final PostRepository<Post> postRepository;
    private final ApplicationEventPublisher eventPublisher;

    /*
     *  조회 ->분기 ->저장 : 동시성 이슈 발생할 수 있음. 분기 처리중 저장했다면?
     * redis에서 꺼내서 비교하는게 좋을 듯
     * */
    @Transactional
    @Override
    public boolean createAndDelete(Long postId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        Post post = postQueryUseCase.findOrThrow(postId);
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
}
