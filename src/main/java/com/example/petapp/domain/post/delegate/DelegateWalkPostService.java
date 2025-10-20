package com.example.petapp.domain.post.delegate;

import com.example.petapp.common.base.embedded.Applicant;
import com.example.petapp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.petapp.domain.post.delegate.model.dto.request.GetPostResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.petapp.domain.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.petapp.domain.walkrecord.model.dto.response.CreateWalkRecordResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface DelegateWalkPostService {
    CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId);

    ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, int page, String email);

    GetPostResponseDto getDelegateWalkPost(Long delegateWalkPostId, String email);

    void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email);

    void deleteDelegateWalkPost(Long delegateWalkPostId, String email);

    Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId);

    CreateChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, String email);

    CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId);
}
