package com.example.petapp.domain.post.delegate;

import com.example.petapp.common.aop.annotation.Notification;
import com.example.petapp.common.base.embedded.Applicant;
import com.example.petapp.common.exception.ForbiddenException;
import com.example.petapp.domain.groupchatroom.ChatRoomService;
import com.example.petapp.domain.groupchatroom.model.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.post.delegate.mapper.DelegateWalkPostMapper;
import com.example.petapp.domain.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.petapp.domain.post.delegate.model.dto.request.GetDelegatePostResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.petapp.domain.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.petapp.domain.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.petapp.domain.post.delegate.model.entity.DelegateWalkPost;
import com.example.petapp.domain.profile.model.entity.Profile;
import com.example.petapp.domain.query.QueryService;
import com.example.petapp.domain.walkrecord.WalkRecordService;
import com.example.petapp.domain.walkrecord.model.dto.response.CreateWalkRecordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class DelegateWalkPostServiceImpl implements DelegateWalkPostService {

    private final DelegateWalkPostRepository delegateWalkPostRepository;
    private final WalkRecordService walkRecordService;
    private final QueryService queryService;
    private final ChatRoomService chatRoomService;

    @Transactional
    @Override
    public CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId) {
        Profile profile = queryService.findByProfile(profileId);
        DelegateWalkPost savedDelegateWalkPost = delegateWalkPostRepository.save(DelegateWalkPostMapper.toEntity(createDelegateWalkPostDto, profile));
        return new CreateDelegateWalkPostResponseDto(savedDelegateWalkPost.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, String email) {
        Member member = queryService.findByMember(email);
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByLocation(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01, pageable).getContent();
        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, int page, String email) {
        Member member = queryService.findByMember(email);
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findByDelegateWalkPostByPlace(longitude, latitude, pageable).getContent();
        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional(readOnly = true)
    @Override
    public GetDelegatePostResponseDto getDelegateWalkPost(Long delegateWalkPostId, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        if (delegateWalkPost.filtering(member)) {
            throw new ForbiddenException("프로필 등록해주세요.");
        }
        return DelegateWalkPostMapper.toGetPostResponseDto(delegateWalkPost, member);
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, String email) {
        Member member = queryService.findByMember(email);
        Member applicantMember = queryService.findByMember(memberId);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        delegateWalkPost.validatedAndSelectApplicant(memberId, member);
        //켈린더에 넣는 로직필요.
        return chatRoomService.createChatRoom(member, applicantMember);
    }

    @Transactional//산책 허가.
    @Override
    public CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        delegateWalkPost.validatedUser(profileId);
        delegateWalkPost.grantAuthorize();//산책 start 허가.
        return walkRecordService.createWalkRecord(delegateWalkPost);
    }

    @Transactional
    @Override
    public void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        delegateWalkPost.validatedUser(member);
        delegateWalkPost.updateDelegateWalkPost(updateDelegateWalkPostDto);
    }

    @Transactional
    @Override
    public void deleteDelegateWalkPost(Long delegateWalkPostId, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        delegateWalkPost.validatedUser(member);
        delegateWalkPostRepository.deleteById(delegateWalkPostId);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        return delegateWalkPost.validatedAndGetApplicants(profileId);
    }

    @Notification(recipient = "@queryService.findByDelegateWalkPost(#p0).profile.member", message = "@queryService.findByMember(#p2).name + '님이 회원님의 대리산책자 게시글에 지원했습니다.'")
    @Transactional
    @Override
    public ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, String email) {
        Member member = queryService.findByMember(email);
        DelegateWalkPost delegateWalkPost = queryService.findByDelegateWalkPost(delegateWalkPostId);
        delegateWalkPost.apply(member, content);
        return new ApplyToDelegateWalkPostResponseDto(member.getId());
    }
    //todo : 자신의 글에 대리산책 지원을 하지못하게 해야됨.
}

