package com.example.petapp.application.service.comment;

import com.example.petapp.application.in.comment.CommentQueryUseCase;
import com.example.petapp.application.in.comment.CommentUseCase;
import com.example.petapp.application.in.comment.dto.request.CommentDto;
import com.example.petapp.application.in.comment.dto.request.UpdateCommentDto;
import com.example.petapp.application.in.comment.dto.response.CreateCommentResponseDto;
import com.example.petapp.application.in.comment.dto.response.GetCommentsResponseDto;
import com.example.petapp.application.in.comment.mapper.CommentMapper;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.post.PostQueryUseCase;
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
    private final CommentQueryUseCase commentQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final PostQueryUseCase<Post> postQueryUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    @Override
    public List<GetCommentsResponseDto> getComments(Long postId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        Post post = postQueryUseCase.findOrThrow(postId);

        return CommentMapper.toGetCommentsResponseDtos((Commentable) post, member);
    }

    @Transactional
    @Override
    public CreateCommentResponseDto createComment(CommentDto commentDto, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        Post post = postQueryUseCase.findOrThrow(commentDto.getPostId());

        Comment comment = CommentMapper.toEntity(commentDto, post, member);
        commentRepository.save(comment);

        eventPublisher.publishEvent(new NotificationEvent(post.getMember().getId(), member.getName() + "님이 회원님의 게시물에 댓글을 남겼습니다."));

        return new CreateCommentResponseDto(comment.getId());
    }


    @Transactional
    @Override
    public void deleteComment(Long commentId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        Comment comment = commentQueryUseCase.findOrThrow(commentId);

        comment.validated(member);
        commentRepository.delete(commentId);
    }

    @Transactional
    @Override
    public void updateComment(Long commentId, UpdateCommentDto updateCommentDto, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        Comment comment = commentQueryUseCase.findOrThrow(commentId);

        comment.validated(member);
        comment.update(updateCommentDto.getContent());

    }
}
