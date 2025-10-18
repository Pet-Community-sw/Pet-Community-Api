package com.example.petapp.domain.comment;


import com.example.petapp.common.base.dto.MessageResponse;
import com.example.petapp.common.base.util.AuthUtil;
import com.example.petapp.domain.comment.model.dto.request.CommentDto;
import com.example.petapp.domain.comment.model.dto.request.UpdateCommentDto;
import com.example.petapp.domain.comment.model.dto.response.CreateCommentResponseDto;
import com.example.petapp.domain.comment.model.dto.response.GetCommentsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Comment")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "댓글 목록 조회"
    )
    @GetMapping("/{postId}")
    private List<GetCommentsResponseDto> getComments(@PathVariable Long postId, Authentication authentication) {
        return commentService.getComments(postId, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "댓글 생성"
    )
    @PostMapping()
    public CreateCommentResponseDto createComment(@RequestBody @Valid CommentDto commentDto, Authentication authentication) {
        return commentService.createComment(commentDto, AuthUtil.getEmail(authentication));
    }

    @Operation(
            summary = "댓글 삭제"
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        commentService.deleteComment(commentId, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("삭제 되었습니다."));
    }

    @Operation(
            summary = "댓글 목록 수정"
    )
    @PutMapping("/{commentId}")//좋아요 개수는 따로하는게 좋을 듯
    public ResponseEntity<MessageResponse> updateComment(@PathVariable Long commentId, @RequestBody @Valid UpdateCommentDto updateCommentDto, Authentication authentication) {
        commentService.updateComment(commentId, updateCommentDto, AuthUtil.getEmail(authentication));
        return ResponseEntity.ok(new MessageResponse("수정 되었습니다."));
    }
}
