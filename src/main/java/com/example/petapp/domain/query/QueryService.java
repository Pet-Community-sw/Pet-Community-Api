package com.example.petapp.domain.query;

import com.example.petapp.domain.comment.model.entity.Comment;
import com.example.petapp.domain.groupchatroom.model.entity.ChatRoom;
import com.example.petapp.domain.petbreed.model.entity.PetBreed;
import com.example.petapp.domain.post.Post;
import com.example.petapp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.petapp.domain.post.normal.model.NormalPost;
import com.example.petapp.domain.post.recommend.model.entity.RecommendRoutePost;
import com.example.petapp.domain.review.model.entity.Review;
import com.example.petapp.domain.walkingtogethermatch.model.entity.WalkingTogetherMatch;
import com.example.petapp.domain.walkrecord.model.entity.WalkRecord;

public interface QueryService {

    ChatRoom findByChatRoom(Long chatRoomId);

    Comment findByComment(Long commentId);

    DelegateWalkPost findByDelegateWalkPost(Long postId);

    NormalPost findByNormalPost(Long postId);

    PetBreed findByPetBreed(String petBreed);

    PetBreed findByPetBreed(Long petBreedId);

    Post findByPost(Long postId);

    RecommendRoutePost findByRecommendRoutePost(Long recommendRoutePostId);

    Review findByReview(Long reviewId);

    WalkingTogetherMatch findByWalkingTogetherPost(Long walkingTogetherPostId);

    WalkRecord findByWalkRecord(Long walkRecordId);
}
