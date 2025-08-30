package com.example.PetApp.service.post.delegate;

import com.example.PetApp.domain.*;
import com.example.PetApp.domain.embedded.Applicant;
import com.example.PetApp.domain.post.DelegateWalkPost;
import com.example.PetApp.domain.post.DelegateWalkStatus;
import com.example.PetApp.dto.delegateWalkpost.*;
import com.example.PetApp.dto.memberchat.CreateMemberChatRoomResponseDto;
import com.example.PetApp.dto.walkrecord.CreateWalkRecordResponseDto;
import com.example.PetApp.exception.ConflictException;
import com.example.PetApp.exception.ForbiddenException;
import com.example.PetApp.mapper.DelegateWalkPostMapper;
import com.example.PetApp.repository.jpa.DelegateWalkPostRepository;
import com.example.PetApp.service.memberchatRoom.MemberChatRoomService;
import com.example.PetApp.service.query.QueryService;
import com.example.PetApp.service.walkrecord.WalkRecordService;
import com.example.PetApp.util.SendNotificationUtil;
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
         if (DelegateWalkPostMapper.filter(delegateWalkPost, member)) {
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
        delegateWalkPost.setStatus(DelegateWalkStatus.COMPLETED);
        delegateWalkPost.setSelectedApplicantMemberId(memberId);
        //켈린더에 넣는 로직필요.
        sendNotification(applicantMember, "대리산책자 지원에 선정되었습니다.");
        return memberChatRoomService.createMemberChatRoom(member, applicantMember);
    }

    @Transactional//산책 허가.
    @Override
    public CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        validateProfile(delegateWalkPost, profileId);
        delegateWalkPost.setStartAuthorized(true);//산책 start 허가.
        return walkRecordService.createWalkRecord(delegateWalkPost);
    }

    @Transactional
    @Override
    public void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);

        validatedMember(delegateWalkPost, member);
        DelegateWalkPostMapper.updateDelegateWalkPost(updateDelegateWalkPostDto, delegateWalkPost);
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

        validateApply(delegateWalkPost, member);
        delegateWalkPost.getApplicants().add(Applicant.builder()
                .memberId(member.getId())
                .content(content)
                .build());
        sendToDelegateWalkPostNotification(member, delegateWalkPost);
        return new ApplyToDelegateWalkPostResponseDto(member.getId());
    }

    private static void validateProfile(DelegateWalkPost delegateWalkPost, Long profileId) {
        if (!(delegateWalkPost.getProfile().getId().equals(profileId))) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    private static void validatedMember(DelegateWalkPost delegateWalkPost, Member member) {
        if (!(delegateWalkPost.getProfile().getMember().equals(member))) {
            throw new ForbiddenException("권한 없음.");
        }
    }

    private static void validateApply(DelegateWalkPost delegateWalkPost, Member member) {
        if (DelegateWalkPostMapper.filter(delegateWalkPost, member)) {
            throw new ForbiddenException("프로필 등록해주세요.");
        } else if (delegateWalkPost.getApplicants().stream().
                anyMatch(applicant -> applicant.getMemberId().equals(member.getId()))) {
            throw new ConflictException("이미 신청한 회원입니다.");
        } else if (delegateWalkPost.getStatus() == DelegateWalkStatus.COMPLETED) {
            throw new ConflictException("모집 완료 게시글입니다.");
        }
    }

    private static void validateSelect(Long memberId, DelegateWalkPost delegateWalkPost, Member member) {
        if (!(delegateWalkPost.getProfile().getMember().equals(member))) {
            throw new ForbiddenException("권한 없음.");
        } else if (delegateWalkPost.getApplicants().stream().noneMatch(applicant -> applicant.getMemberId().equals(memberId))) {
            throw new ConflictException("해당 지원자는 없습니다.");
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

