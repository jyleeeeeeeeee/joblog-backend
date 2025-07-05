package com.joblog.comment.service;

import com.joblog.comment.domain.Comment;
import com.joblog.comment.dto.CommentRequest;
import com.joblog.comment.dto.CommentResponse;
import com.joblog.comment.repository.CommentRepository;
import com.joblog.common.exception.UnauthorizedException;
import com.joblog.post.domain.Post;
import com.joblog.post.repository.PostRepository;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Comment create(User user, CommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .build();

        post.addComment(comment);

        return commentRepository.save(comment);
    }


    @Override
    public void update(User user, Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        validateOwner(user, comment);

        comment.update(comment.getContent());
    }

    @Override
    public void delete(User user, Long commentId) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        validateOwner(user, comment);

        comment.delete();
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }

        return commentRepository.findAllByPostId(postId)
                .stream().map(CommentResponse::new).toList();
    }

    /**
     * 작성자 확인
     * @param comment
     */
    private void validateOwner(User user, Comment comment) {
        if(!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자만 댓글을 수정/삭제할 수 있습니다.");
        }
    }
}
