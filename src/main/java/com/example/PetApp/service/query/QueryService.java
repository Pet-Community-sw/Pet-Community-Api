package com.example.PetApp.service.query;

import com.example.PetApp.domain.*;
import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.domain.post.NormalPost;
import com.example.PetApp.domain.post.Post;
import com.example.PetApp.domain.post.RecommendRoutePost;

public interface QueryService {
    Member findbyMember(String email);

    Member findbyMember(Long memberId);

    ChatRoom findByChatRoom(Long chatRoomId);

    Comment findByComment(Long commentId);

    DelegateWalkPost findByDelegateWalkPost(Long postId);

    MemberChatRoom findByMemberChatRoom(Long memberChatRoomId);

    NormalPost findByNormalPost(Long postId);

    PetBreed findByPetBreed(String petBreed);

    Post findByPost(Long postId);

    Profile findByProfile(Long profileId);

    RecommendRoutePost findByRecommendRoutePost(Long recommendRoutePostId);

    Review findByReview(Long reviewId);

    WalkingTogetherPost findByWalkingTogetherPost(Long walkingTogetherPostId);

    WalkRecord findByWalkRecord(Long walkRecordId);
}
