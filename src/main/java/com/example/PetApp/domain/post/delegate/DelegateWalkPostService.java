package com.example.PetApp.domain.post.delegate;

import com.example.PetApp.common.base.embedded.Applicant;
import com.example.PetApp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.GetPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.PetApp.domain.walkrecord.model.dto.response.CreateWalkRecordResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface DelegateWalkPostService {
    CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId);

    ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email);

    List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, String email);

    GetPostResponseDto getDelegateWalkPost(Long delegateWalkPostId, String email);

    void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email);

    void deleteDelegateWalkPost(Long delegateWalkPostId, String email);

    Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId);

    CreateChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, String email);

    CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId);
}
