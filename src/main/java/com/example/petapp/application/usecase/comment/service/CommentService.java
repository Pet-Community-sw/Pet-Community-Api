package com.example.petapp.application.usecase.comment.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.comment.CommentUseCase;
import com.example.petapp.application.usecase.comment.dto.request.CommentDto;
import com.example.petapp.application.usecase.comment.dto.request.UpdateCommentDto;
import com.example.petapp.application.usecase.comment.dto.response.CreateCommentResponseDto;
import com.example.petapp.application.usecase.comment.dto.response.GetCommentsResponseDto;
import com.example.petapp.application.usecase.comment.mapper.CommentMapper;
import com.example.petapp.application.usecase.member.MemberUseCase;
import com.example.petapp.application.usecase.notification.dto.NotificationEvent;
import com.example.petapp.application.usecase.post.PostUseCase;
import com.example.petapp.domain.comment.CommentRepository;
import com.example.petapp.domain.comment.model.Comment;
import com.example.petapp.domain.comment.model.Commentable;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor//memberId를 받아서 유효성 검사해야할듯.
public class CommentService implements CommentUseCase {

    private final CommentRepository commentRepository;
    private final MemberUseCase memberUseCase;
    private final PostUseCase<Post> postUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    @Override
    public List<GetCommentsResponseDto> getComments(Long postId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Post post = postUseCase.findOrThrow(postId);

        return CommentMapper.toGetCommentsResponseDtos((Commentable) post, member);
    }

    @Transactional
    @Override
    public CreateCommentResponseDto createComment(CommentDto commentDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Post post = postUseCase.findOrThrow(commentDto.getPostId());

        Comment comment = CommentMapper.toEntity(commentDto, post, member);
        commentRepository.save(comment);

        eventPublisher.publishEvent(new NotificationEvent(post.getMember().getId(), member.getName() + "님이 회원님의 게시물에 댓글을 남겼습니다."));

        return new CreateCommentResponseDto(comment.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Comment findOrThrow(Long id) {
        return commentRepository.find(id).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 댓글은 없습니다."));
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Comment comment = findOrThrow(commentId);

        comment.validated(member);
        commentRepository.delete(commentId);
    }

    @Transactional
    @Override
    public void updateComment(Long commentId, UpdateCommentDto updateCommentDto, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        Comment comment = findOrThrow(commentId);

        comment.validated(member);
        comment.update(updateCommentDto.getContent());

    }
}
