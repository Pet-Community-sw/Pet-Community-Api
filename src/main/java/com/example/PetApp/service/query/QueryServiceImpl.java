package com.example.PetApp.service.query;

import com.example.PetApp.domain.*;
import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.domain.post.NormalPost;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.post.RecommendRoutePost;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.exception.NotFoundException;
import com.example.PetApp.repository.jpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService{

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CommentRepository commentRepository;
    private final DelegateWalkPostRepository delegateWalkPostRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final NormalPostRepository normalPostRepository;
    private final PetBreedRepository petBreedRepository;
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final RecommendRoutePostRepository recommendRoutePostRepository;
    private final ReviewRepository reviewRepository;
    private final WalkingTogetherPostRepository walkingTogetherPostRepository;
    private final WalkRecordRepository walkRecordRepository;

    @Override
    public Member findbyMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }

    @Override
    public Member findbyMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }

    @Override
    public ChatRoom findByChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new NotFoundException("해당 채팅방은 없습니다."));
    }

    @Override
    public Comment findByComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("해당 댓글은 없습니다."));
    }

    @Override
    public DelegateWalkPost findByDelegateWalkPost(Long postId) {
        return delegateWalkPostRepository.findById(postId).orElseThrow(()->new NotFoundException("해당 대리산책자 게시글은 없습니다."));
    }

    @Override
    public MemberChatRoom findByMemberChatRoom(Long memberChatRoomId) {
        return memberChatRoomRepository.findById(memberChatRoomId).orElseThrow(() -> new NotFoundException("해당 채팅방은 없습니다."));
    }

    @Override
    public NormalPost findByNormalPost(Long postId) {
        return normalPostRepository.findById(postId).orElseThrow(() -> new NotFoundException("해당 자유 게시물은 없습니다."));
    }

    @Override
    public PetBreed findByPetBreed(String petBreed) {
        return petBreedRepository.findByName(petBreed).orElseThrow(() -> new NotFoundException("종을 다시 입력해주세요."));
    }

    @Override
    public Post findByPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("해당 게시물은 없습니다."));
    }

    @Override
    public Profile findByProfile(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new ForbiddenException("프로필을 등록해주세요."));
    }

    @Override
    public RecommendRoutePost findByRecommendRoutePost(Long recommendRoutePostId) {
        return recommendRoutePostRepository.findById(recommendRoutePostId).orElseThrow(() -> new NotFoundException("해당 산책길 추천 게시글은 없습니다."));
    }

    @Override
    public Review findByReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException("해당 리뷰는 없습니다."));
    }

    @Override
    public WalkingTogetherPost findByWalkingTogetherPost(Long walkingTogetherPostId) {
        return walkingTogetherPostRepository.findById(walkingTogetherPostId).orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }

    @Override
    public WalkRecord findByWalkRecord(Long walkRecordId) {
        return walkRecordRepository.findById(walkRecordId).orElseThrow(() -> new NotFoundException("해당 산책기록은 없습니다."));
    }
}
