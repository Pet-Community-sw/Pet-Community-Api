package com.example.petapp.application.in.post.recommend;

import com.example.petapp.application.in.post.recommend.dto.request.CreateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.request.UpdateRecommendRoutePostDto;
import com.example.petapp.application.in.post.recommend.dto.response.CreateRecommendRoutePostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendPostResponseDto;
import com.example.petapp.application.in.post.recommend.dto.response.GetRecommendRoutePostsResponseDto;

import java.util.List;

public interface RecommendRoutePostUseCase {
    CreateRecommendRoutePostResponseDto createRecommendRoutePost(CreateRecommendRoutePostDto createRecommendRoutePostDto, Long id);

    List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, Long id);

    List<GetRecommendRoutePostsResponseDto> getRecommendRoutePosts(Double longitude, Double latitude, int page, Long id);

    GetRecommendPostResponseDto getRecommendRoutePost(Long recommendRoutePostId, Long id);

    void updateRecommendRoutePost(Long recommendRoutePostId, UpdateRecommendRoutePostDto updateRecommendRoutePostDto, Long id);


    void deleteRecommendRoutePost(Long recommendRoutePostId, Long id);
}
