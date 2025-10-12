package com.example.PetApp.domain.query;

import com.example.PetApp.domain.comment.model.entity.Comment;
import com.example.PetApp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.post.normal.model.entity.NormalPost;
import com.example.PetApp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.domain.review.model.entity.Review;
import com.example.PetApp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;

public interface QueryService {
    Member findByMember(String email);

    Member findByMember(Long memberId);

    Member findByMemberToPhoneNumber(String phoneNumber);

    ChatRoom findByChatRoom(Long chatRoomId);

    Comment findByComment(Long commentId);

    DelegateWalkPost findByDelegateWalkPost(Long postId);
    
    NormalPost findByNormalPost(Long postId);

    PetBreed findByPetBreed(String petBreed);

    PetBreed findByPetBreed(Long petBreedId);

    Post findByPost(Long postId);

    Profile findByProfile(Long profileId);

    RecommendRoutePost findByRecommendRoutePost(Long recommendRoutePostId);

    Review findByReview(Long reviewId);

    WalkingTogetherMatch findByWalkingTogetherPost(Long walkingTogetherPostId);

    WalkRecord findByWalkRecord(Long walkRecordId);
}
