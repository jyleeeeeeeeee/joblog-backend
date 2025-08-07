package com.joblog.comment.service;

import com.joblog.comment.domain.Comment;
import com.joblog.comment.dto.CommentRequest;
import com.joblog.comment.dto.CommentResponse;
import com.joblog.comment.repository.CommentRepository;
import com.joblog.notification.domain.NotificationType;
import com.joblog.notification.kafka.event.NotificationEvent;
import com.joblog.notification.kafka.producer.NotificationKafkaProducer;
import com.joblog.post.domain.Post;
import com.joblog.post.repository.PostRepository;
import com.joblog.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationKafkaProducer notificationKafkaProducer;

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

        Comment saved = commentRepository.save(comment);
        Long postOwnerId = saved.getPost().getUser().getId();


        if(!user.getId().equals(postOwnerId)) {
            NotificationEvent event = NotificationEvent.builder()
                    .receiverId(postOwnerId)
                    .content("새 댓글이 달렸습니다: " + saved.getContent())
                    .type(NotificationType.COMMENT)
                    .build();

            notificationKafkaProducer.send(event);
        }

        return saved;
    }


    @Override
    public void update(User user, Long commentId, CommentRequest request) {
        Comment comment = validateCommentOwner(user, commentId);
        comment.update(comment.getContent());
    }

    @Override
    public void delete(User user, Long commentId) {
        Comment comment = validateCommentOwner(user, commentId);
        comment.delete();
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
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
     *
     * @param user User
     * @param commentId Long
     * @return comment Comment
     */
    private Comment validateCommentOwner(User user, Long commentId) {
        Comment comment = findComment(commentId);

        if(!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자만 댓글을 수정/삭제할 수 있습니다.");
        }
        return comment;
    }
}
