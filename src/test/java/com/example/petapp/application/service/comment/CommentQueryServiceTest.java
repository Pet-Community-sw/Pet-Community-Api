package com.example.petapp.application.service.comment;

import com.example.petapp.domain.comment.CommentRepository;
import com.example.petapp.domain.comment.model.Comment;
import com.example.petapp.interfaces.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentQueryServiceTest {

    @Mock
    private CommentRepository repository;

    @InjectMocks
    private CommentQueryService commentQueryService;

    @Test
    void 댓글이_존재하면_조회에_성공한다() {
        Comment comment = org.mockito.Mockito.mock(Comment.class);
        when(repository.find(1L)).thenReturn(Optional.of(comment));

        Comment result = commentQueryService.findOrThrow(1L);

        assertThat(result).isSameAs(comment);
        verify(repository).find(1L);
    }

    @Test
    void 댓글이_없으면_예외가_발생한다() {
        when(repository.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentQueryService.findOrThrow(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 댓글은 없습니다.");
    }
}
