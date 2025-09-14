package com.example.PetApp.domain.post.delegate;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.GetPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.PetApp.domain.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.PetApp.domain.profile.model.entity.Profile;
import com.example.PetApp.infrastructure.database.shared.embedded.Applicant;
import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.PetApp.domain.post.delegate.model.entity.DelegateWalkStatus;
import com.example.PetApp.domain.memberchatRoom.model.dto.response.CreateMemberChatRoomResponseDto;
import com.example.PetApp.domain.walkrecord.model.dto.response.CreateWalkRecordResponseDto;
import com.example.PetApp.common.exception.ConflictException;
import com.example.PetApp.common.exception.ForbiddenException;
import com.example.PetApp.domain.post.delegate.mapper.DelegateWalkPostMapper;
import com.example.PetApp.domain.memberchatRoom.MemberChatRoomService;
import com.example.PetApp.domain.query.QueryService;
import com.example.PetApp.domain.walkrecord.WalkRecordService;
import com.example.PetApp.common.util.SendNotificationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class DelegateWalkPostServiceImpl implements DelegateWalkPostService {

    private final DelegateWalkPostRepository delegateWalkPostRepository;
    private final MemberChatRoomService memberChatRoomService;
    private final WalkRecordService walkRecordService;
    private final SendNotificationUtil sendNotificationUtil;
    private final QueryService queryService;

    @Transactional
    @Override
    public CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId) {
        Profile profile = queryService.findByProfile(profileId);
        DelegateWalkPost savedDelegateWalkPost = delegateWalkPostRepository.save(DelegateWalkPostMapper.toEntity(createDelegateWalkPostDto, profile));
        return new CreateDelegateWalkPostResponseDto(savedDelegateWalkPost.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, String email) {
        Member member = queryService.findByMember(email);
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01);
        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, String email) {
        Member member = queryService.findByMember(email);
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByPlace(longitude, latitude);

        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional(readOnly = true)
    @Override
    public GetPostResponseDto getDelegateWalkPost(Long delegateWalkPostId, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        if (delegateWalkPost.filtering(member)) {
            throw new ForbiddenException("프로필 등록해주세요.");
        }
        return DelegateWalkPostMapper.toGetPostResponseDto(delegateWalkPost);
    }

    @Transactional
    @Override
    public CreateMemberChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, String email) {
        Member member = queryService.findByMember(email);
        Member applicantMember = queryService.findByMember(memberId);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        validateSelect(memberId, delegateWalkPost, member);
        delegateWalkPost.updateStatusAndSelectedApplicantMemberId(memberId);
        //켈린더에 넣는 로직필요.
        sendNotification(applicantMember, "대리산책자 지원에 선정되었습니다.");
        return memberChatRoomService.createMemberChatRoom(member, applicantMember);
    }

    @Transactional//산책 허가.
    @Override
    public CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        validateProfile(delegateWalkPost, profileId);
        delegateWalkPost.grantAuthorize();//산책 start 허가.
        return walkRecordService.createWalkRecord(delegateWalkPost);
    }

    @Transactional
    @Override
    public void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);

        validatedMember(delegateWalkPost, member);
        delegateWalkPost.updateDelegateWalkPost(updateDelegateWalkPostDto);
    }

    @Transactional
    @Override
    public void deleteDelegateWalkPost(Long delegateWalkPostId, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);

        validatedMember(delegateWalkPost, member);
        delegateWalkPostRepository.deleteById(delegateWalkPostId);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        validateProfile(delegateWalkPost, profileId);

        return delegateWalkPost.getApplicants();
    }

    @Transactional
    @Override
    public ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);

        delegateWalkPost.apply(member, content);
        sendToDelegateWalkPostNotification(member, delegateWalkPost);
        return new ApplyToDelegateWalkPostResponseDto(member.getId());
    }

    private static void validateProfile(DelegateWalkPost delegateWalkPost, Long profileId) {
        if (delegateWalkPost.validatedUser(profileId)) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    private static void validatedMember(DelegateWalkPost delegateWalkPost, Member member) {
        if (delegateWalkPost.validatedUser(member)) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    private static void validateSelect(Long memberId, DelegateWalkPost delegateWalkPost, Member member) {
        if (delegateWalkPost.validatedUser(member.getId()) && delegateWalkPost.validatedApplicantsInMember(memberId)) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    private void sendToDelegateWalkPostNotification(Member member, DelegateWalkPost delegateWalkPost) {
        String message = member.getName() + "님이 회원님의 대리산책자 게시글에 지원했습니다.";
        sendNotification(delegateWalkPost.getProfile().getMember(), message);

    }

    private void sendNotification(Member member, String message) {
        sendNotificationUtil.sendNotification(member, message);
    }
}

