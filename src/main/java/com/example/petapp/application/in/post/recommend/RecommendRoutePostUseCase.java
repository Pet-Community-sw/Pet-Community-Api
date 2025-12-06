package com.example.petapp.application.in.post.recommend;

import com.example.petapp.application.in.post.recommend.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.request.UpdateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.response.CreateRecommendRoutePostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendRoutePostsResponseDto;

import java.util.List;

public interface RecommendRoutePostUseCase {
    CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, String email);

    List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email);

    List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, String email);

    GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, String email);

    void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, String email);


    void deleteRecommendRoutePost(Long recommendRoutePostId, String email);
}
