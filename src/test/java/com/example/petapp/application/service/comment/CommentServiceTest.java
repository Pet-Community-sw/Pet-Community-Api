package com.example.petapp.application.service.comment;

import com.example.petapp.application.in.comment.CommentUseCase;
import com.example.petapp.application.in.comment.dto.response.GetCommentsResponseDto;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.post.PostUseCase;
import com.example.petapp.domain.comment.CommentRepository;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.post.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentUseCase commentUseCase;

    @Mock
    private MemberUseCase memberUseCase;

    @Mock
    private PostUseCase<Post> postUseCase;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CommentService commentService;

    @Test
    void 대리산책_게시글도_댓글목록_조회가_정상동작한다() {
        Member member = org.mockito.Mockito.mock(Member.class);
        DelegateWalkPost delegateWalkPost = org.mockito.Mockito.mock(DelegateWalkPost.class);

        when(memberUseCase.findOrThrow(1L)).thenReturn(member);
        when(postUseCase.findOrThrow(10L)).thenReturn(delegateWalkPost);
        when(delegateWalkPost.getComments()).thenReturn(List.of());

        List<GetCommentsResponseDto> result = commentService.getComments(10L, 1L);

        assertThat(result).isEmpty();
    }
}
