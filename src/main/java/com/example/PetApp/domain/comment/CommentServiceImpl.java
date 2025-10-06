package com.example.PetApp.domain.comment;

import com.example.PetApp.common.aop.annotation.Notification;
import com.example.PetApp.common.base.util.notification.NotificationDto;
import com.example.PetApp.domain.comment.model.dto.request.CommentDto;
import com.example.PetApp.domain.comment.model.dto.request.UpdateCommentDto;
import com.example.PetApp.domain.comment.model.dto.response.CreateCommentResponseDto;
import com.example.PetApp.domain.comment.model.dto.response.GetCommentsResponseDto;
import com.example.PetApp.domain.comment.model.entity.Comment;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.post.common.Post;
import com.example.PetApp.domain.comment.model.entity.Commentable;
import com.example.PetApp.domain.comment.mapper.CommentMapper;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor//memberId를 받아서 유효성 검사해야할듯.
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final QueryService queryService;

    @Transactional(readOnly = true)
    @Override
    public List<GetCommentsResponseDto> getComments(Long postId, String email) {
        Member member = queryService.findByMember(email);
        Post post = queryService.findByPost(postId);

        return CommentMapper.toGetCommentsResponseDtos((Commentable) post, member);
    }

    @Notification(recipient = "#ret.notificationDto.ownerMember", message = "#ret.notification.member.name + '님이 회원님의 게시물에 댓글을 남겼습니다.'")
    @Transactional
    @Override
    public CreateCommentResponseDto createComment(CommentDto commentDto, String email) {
        Member member = queryService.findByMember(email);
        Post post = queryService.findByPost(commentDto.getPostId());

        Comment comment = CommentMapper.toEntity(commentDto, post, member);
        commentRepository.save(comment);
        return new CreateCommentResponseDto(comment.getId(), new NotificationDto(post.getMember(), member));
    }


    @Transactional
    @Override
    public void deleteComment(Long commentId, String email) {
        Member member = queryService.findByMember(email);
        Comment comment = queryService.findByComment(commentId);

        comment.validated(member);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public void updateComment(Long commentId, UpdateCommentDto updateCommentDto, String email) {
        Member member = queryService.findByMember(email);
        Comment comment = queryService.findByComment(commentId);

        comment.validated(member);
        comment.update(updateCommentDto.getContent());

    }
}
