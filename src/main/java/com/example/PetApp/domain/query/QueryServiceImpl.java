package com.example.PetApp.domain.query;

import com.example.PetApp.domain.comment.CommentRepository;
import com.example.PetApp.domain.groupchatroom.ChatRoomRepository;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.comment.model.entity.Comment;
import com.example.PetApp.domain.member.MemberRepository;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.MemberChatRoomRepository;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.domain.petbreed.PetBreedRepository;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import com.example.PetApp.domain.post.common.PostRepository;
import com.example.PetApp.domain.post.delegate.DelegateWalkPostRepository;
import com.example.PetApp.domain.post.normal.NormalPostRepository;
import com.example.PetApp.domain.post.recommend.RecommendRoutePostRepository;
import com.example.PetApp.domain.profile.ProfileRepository;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.review.ReviewRepository;
import com.example.PetApp.domain.review.model.entity.Review;
import com.example.PetApp.domain.walkingtogethermatch.WalkingTogetherMatchRepository;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.walkrecord.WalkRecordRepository;
import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;
import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.post.normal.model.entity.NormalPost;
import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.common.exception.NotFoundException;
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
    private final WalkingTogetherMatchRepository walkingTogetherMatchRepository;
    private final WalkRecordRepository walkRecordRepository;

    @Override
    public Member findByMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }

    @Override
    public Member findByMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("해당 유저는 없습니다."));
    }

    @Override
    public Member findByMemberToPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new NotFoundException("해당 유저는 없는 유저입니다. 회원가입 해주세요."));
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
    public WalkingTogetherMatch findByWalkingTogetherPost(Long walkingTogetherPostId) {
        return walkingTogetherMatchRepository.findById(walkingTogetherPostId).orElseThrow(() -> new NotFoundException("해당 함께 산책해요 게시글은 없습니다."));
    }

    @Override
    public WalkRecord findByWalkRecord(Long walkRecordId) {
        return walkRecordRepository.findById(walkRecordId).orElseThrow(() -> new NotFoundException("해당 산책기록은 없습니다."));
    }
}
