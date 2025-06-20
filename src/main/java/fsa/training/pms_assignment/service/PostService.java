package fsa.training.pms_assignment.service;

import fsa.training.pms_assignment.dto.request.Post.CreatePostRequest;
import fsa.training.pms_assignment.dto.request.Post.UpdatePostRequest;
import fsa.training.pms_assignment.dto.response.post.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostService {
    PostResponse createPost(CreatePostRequest request);

    PostResponse updatePost(UUID id, UpdatePostRequest request);

    PostResponse getPostById(UUID id);

    void deletePost(UUID id);

    List<PostResponse> getAllPublishedPosts();

    List<PostResponse> getPostsByCategory(UUID categoryId);

    List<PostResponse> searchPostsByTitle(String keyword);

    List<PostResponse> getPostsByAuthor(String author);

    Page<PostResponse> getAllPosts(Pageable pageable);

    void changePostToPublish(UUID id, boolean published);

}
