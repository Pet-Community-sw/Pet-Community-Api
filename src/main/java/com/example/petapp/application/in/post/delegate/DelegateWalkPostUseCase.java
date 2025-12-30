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

    ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, Long id);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, Long id);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, int page, Long id);

    GetDelegatePostResponseDto getDelegateWalkPost(Long delegateWalkPostId, Long id);

    void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, Long id);

    void deleteDelegateWalkPost(Long delegateWalkPostId, Long id);

    Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId);

    CreateChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, Long id);

    CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId);
}
