package com.example.petapp.domain.comment;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.post.PostQueryUseCase;
import com.example.petapp.common.aop.annotation.Notification;
import com.example.petapp.domain.comment.mapper.CommentMapper;
import com.example.petapp.domain.comment.model.dto.request.CommentDto;
import com.example.petapp.domain.comment.model.dto.request.UpdateCommentDto;
import com.example.petapp.domain.comment.model.dto.response.CreateCommentResponseDto;
import com.example.petapp.domain.comment.model.dto.response.GetCommentsResponseDto;
import com.example.petapp.domain.comment.model.entity.Comment;
import com.example.petapp.domain.comment.model.entity.Commentable;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.Post;
import com.example.petapp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor//memberId를 받아서 유효성 검사해야할듯.
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final QueryService queryService;
    private final MemberQueryUseCase memberQueryUseCase;
    private final PostQueryUseCase<Post> postQueryUseCase;

    @Transactional(readOnly = true)
    @Override
    public List<GetCommentsResponseDto> getComments(Long postId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Post post = postQueryUseCase.findOrThrow(postId);

        return CommentMapper.toGetCommentsResponseDtos((Commentable) post, member);
    }

    @Notification(recipient = "@queryService.findByPost(#p0.postId).member", message = "@queryService.findByMember(#1).name + '님이 회원님의 게시물에 댓글을 남겼습니다.'")
    @Transactional
    @Override
    public CreateCommentResponseDto createComment(CommentDto commentDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Post post = postQueryUseCase.findOrThrow(commentDto.getPostId());

        Comment comment = CommentMapper.toEntity(commentDto, post, member);
        commentRepository.save(comment);
        return new CreateCommentResponseDto(comment.getId());
    }


    @Transactional
    @Override
    public void deleteComment(Long commentId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Comment comment = queryService.findByComment(commentId);

        comment.validated(member);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public void updateComment(Long commentId, UpdateCommentDto updateCommentDto, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        Comment comment = queryService.findByComment(commentId);

        comment.validated(member);
        comment.update(updateCommentDto.getContent());

    }
}
