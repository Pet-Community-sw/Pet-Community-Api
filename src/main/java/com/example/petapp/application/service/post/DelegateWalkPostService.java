package com.example.petapp.application.service.post;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatroom.dto.response.CreateChatRoomResponseDto;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.post.PostUseCase;
import com.example.petapp.application.in.post.delegate.DelegateWalkPostUseCase;
import com.example.petapp.application.in.post.delegate.mapper.DelegateWalkPostMapper;
import com.example.petapp.application.in.post.delegate.model.dto.request.CreateDelegateWalkPostDto;
import com.example.petapp.application.in.post.delegate.model.dto.request.GetDelegatePostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.request.UpdateDelegateWalkPostDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.ApplyToDelegateWalkPostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.CreateDelegateWalkPostResponseDto;
import com.example.petapp.application.in.post.delegate.model.dto.response.GetDelegateWalkPostsResponseDto;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.in.walkrecord.WalkRecordUseCase;
import com.example.petapp.application.in.walkrecord.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.DelegateWalkPostRepository;
import com.example.petapp.domain.post.PostRepository;
import com.example.petapp.domain.post.model.Applicant;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.interfaces.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class DelegateWalkPostService implements DelegateWalkPostUseCase {

    private final DelegateWalkPostRepository delegateWalkPostRepository;
    private final WalkRecordUseCase walkRecordUseCase;
    private final ProfileUseCase profileUseCase;
    private final MemberUseCase memberUseCase;
    private final ChatRoomUseCase chatRoomUseCase;
    private final PostUseCase<DelegateWalkPost> postUseCase;
    private final PostRepository<DelegateWalkPost> postRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public CreateDelegateWalkPostResponseDto createDelegateWalkPost(CreateDelegateWalkPostDto createDelegateWalkPostDto, Long profileId) {
        Profile profile = profileUseCase.findOrThrow(profileId);
        DelegateWalkPost savedDelegateWalkPost = postRepository.save(DelegateWalkPostMapper.toEntity(createDelegateWalkPostDto, profile));
        return new CreateDelegateWalkPostResponseDto(savedDelegateWalkPost.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByLocation(Double minLongitude, Double minLatitude, Double maxLongitude, Double maxLatitude, int page, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findList(minLongitude - 0.01, minLatitude - 0.01, maxLongitude + 0.01, maxLatitude + 0.01, pageable).getContent();
        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetDelegateWalkPostsResponseDto> getDelegateWalkPostsByPlace(Double longitude, Double latitude, int page, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<DelegateWalkPost> delegateWalkPosts = delegateWalkPostRepository.findList(longitude, latitude, pageable).getContent();
        return DelegateWalkPostMapper.toGetDelegateWalkPostsResponseDtos(member, delegateWalkPosts);
    }

    @Transactional()
    @Override
    public GetDelegatePostResponseDto getDelegateWalkPost(Long delegateWalkPostId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        DelegateWalkPost delegateWalkPost = postUseCase.findOrThrow(delegateWalkPostId);
        if (delegateWalkPost.filtering(member)) {
            throw new ForbiddenException("프로필 등록해주세요.");
        }
        postRepository.incrementViewCount(delegateWalkPostId);
        return DelegateWalkPostMapper.toGetPostResponseDto(delegateWalkPost, member);
    }

    @Transactional
    @Override
    public CreateChatRoomResponseDto selectApplicant(Long delegateWalkPostId, Long memberId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Member applicantMember = memberUseCase.findOrThrow(memberId);
        DelegateWalkPost delegateWalkPost = postUseCase.findOrThrow(delegateWalkPostId);
        delegateWalkPost.validatedAndSelectApplicant(memberId, member);
        //켈린더에 넣는 로직필요.
        return chatRoomUseCase.createChatRoom(member, applicantMember);
    }

    @Transactional//산책 허가.
    @Override
    public CreateWalkRecordResponseDto grantAuthorize(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = postUseCase.findOrThrow(delegateWalkPostId);
        delegateWalkPost.validatedUser(profileId);
        delegateWalkPost.grantAuthorize();//산책 start 허가.
        return walkRecordUseCase.createWalkRecord(delegateWalkPost);
    }

    @Transactional
    @Override
    public void updateDelegateWalkPost(Long delegateWalkPostId, UpdateDelegateWalkPostDto updateDelegateWalkPostDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        DelegateWalkPost delegateWalkPost = postUseCase.findOrThrow(delegateWalkPostId);
        delegateWalkPost.validatedUser(member);
        delegateWalkPost.updateDelegateWalkPost(updateDelegateWalkPostDto);
    }

    @Transactional
    @Override
    public void deleteDelegateWalkPost(Long delegateWalkPostId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        DelegateWalkPost delegateWalkPost = postUseCase.findOrThrow(delegateWalkPostId);
        delegateWalkPost.validatedUser(member);
        postRepository.delete(delegateWalkPostId);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Applicant> getApplicants(Long delegateWalkPostId, Long profileId) {
        DelegateWalkPost delegateWalkPost = postUseCase.findOrThrow(delegateWalkPostId);
        return delegateWalkPost.validatedAndGetApplicants(profileId);
    }

    @Transactional
    @Override
    public ApplyToDelegateWalkPostResponseDto applyToDelegateWalkPost(Long delegateWalkPostId, String content, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        DelegateWalkPost delegateWalkPost = postUseCase.findOrThrow(delegateWalkPostId);
        delegateWalkPost.apply(member, content);

        eventPublisher.publishEvent(new NotificationEvent(delegateWalkPost.getProfile().getMember().getId(), member.getName() + "님이 회원님의 대리산책자 게시글에 지원했습니다."));
        return new ApplyToDelegateWalkPostResponseDto(member.getId());
    }
}

