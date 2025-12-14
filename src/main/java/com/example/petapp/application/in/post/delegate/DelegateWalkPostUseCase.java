package com.example.petapp.application.in.post.delegate;

import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.petapp.application.in.post.delegate.model.dto.request.GetDelegatePostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.domain.post.model.Applicant;

import java.util.List;
import java.util.Set;

public interface DelegateWalkPostUseCase {
    CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId);

    ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, int page, String email);

    GetDelegatePostResponseDto getDelegateWalkPost(Long delegateWalkPostId, String email);

    void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email);

    void deleteDelegateWalkPost(Long delegateWalkPostId, String email);

    Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId);

    CreateChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, String email);

    CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId);
}
