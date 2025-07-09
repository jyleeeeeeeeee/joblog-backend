package com.joblog.post.service;

import com.joblog.post.domain.FileAttachment;
import com.joblog.post.domain.Post;
import com.joblog.post.dto.PostRequest;
import com.joblog.post.dto.PostResponse;
import com.joblog.post.dto.PostSearchCondition;
import com.joblog.post.dto.PostUpdateRequest;
import com.joblog.post.repository.PostRepository;
import com.joblog.user.entity.User;
import com.joblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    @Value("${file.upload-dir}") // application.yml에서 업로드 경로 주입
    private String uploadDir;

    private final PostRepository postRepository;
    private final FileService fileService;

    /**
     * 게시글 작성
     * @param user
     * @return 게시물 id
     */
    @Transactional
    public Long create(PostRequest request, List<MultipartFile> attachments, User user) throws IOException {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
        user.addPost(post);

        if (attachments != null) {
            for (MultipartFile file : attachments) {
                String storedFilename = fileService.saveFile(file);

                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setOriginalFilename(file.getOriginalFilename());
                fileAttachment.setStoredFilename(storedFilename);
                fileAttachment.setFileType(file.getContentType());
                fileAttachment.setFilePath(uploadDir + "/" + storedFilename);

                post.addAttachment(fileAttachment);

            }
        }

        return postRepository.save(post).getId();
    }

    /**
     *
     * @param id
     * @return
     */
    public PostResponse searchPostOne(Long id) {
        Post post = postRepository.searchOne(id).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다"));

        return new PostResponse(post, post.getComments());

    }
    /**
     *
     * @param condition
     * @param pageable
     * @return 게시글 목록
     */
    public Page<PostResponse> searchPost(PostSearchCondition condition, Pageable pageable) {
        return postRepository.search(condition, pageable).map(PostResponse::new);
    }

    /**
     * 게시물 수정
     * @param postId
     * @param request
     * @param loginUser
     */
    @Transactional
    public void update(Long postId, PostUpdateRequest request, User loginUser) {
        Post post = postRepository.findPostForUpdate(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getUser().getId().equals(loginUser.getId())) {
            throw new AccessDeniedException("게시글 수정 권한이 없습니다.");
        }
        post.update(request.getTitle(), request.getContent());
    }

    /**
     * 게시물 삭제
     * @param postId
     * @param loginUser
     */
    @Transactional
    public void delete(Long postId, User loginUser) {
        Post post = postRepository.findPostForDelete(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getUser().getId().equals(loginUser.getId())) {
            throw new AccessDeniedException("게시글 삭제 권한이 없습니다.");
        }

        post.delete();
    }

    @Transactional
    public void forceDelete(Long postId, User loginUser) {
        Post post = postRepository.findPostForDelete(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getUser().getId().equals(loginUser.getId())) {
            throw new AccessDeniedException("게시글 삭제 권한이 없습니다.");
        }
        postRepository.delete(post);
    }
}
