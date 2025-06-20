package fsa.training.pms_assignment.web;


import fsa.training.pms_assignment.dto.request.Post.CreatePostRequest;
import fsa.training.pms_assignment.dto.request.Post.UpdatePostRequest;
import fsa.training.pms_assignment.dto.response.post.PostResponse;
import fsa.training.pms_assignment.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // CREATE
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        PostResponse response = postService.createPost(request);
        if (response == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response);
    }

    // UPDATE

    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable UUID id, @RequestBody UpdatePostRequest request) {
        PostResponse response = postService.updatePost(id, request);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID id) {
        PostResponse response = postService.getPostById(id);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    // DELETE
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // GET ALL PAGED
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    // GET ALL PUBLISHED
    @GetMapping("/published")
    public ResponseEntity<List<PostResponse>> getPublishedPosts() {
        return ResponseEntity.ok(postService.getAllPublishedPosts());
    }

    // SEARCH BY TITLE
    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPostsByTitle(keyword));
    }

    // GET BY AUTHOR
    @GetMapping("/author")
    public ResponseEntity<List<PostResponse>> getPostsByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(postService.getPostsByAuthor(author));
    }

    // GET BY CATEGORY
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostResponse>> getPostsByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId));
    }

    // TOGGLE PUBLISH STATUS
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<Void> changePublishStatus(@PathVariable UUID id, @RequestParam boolean published) {
        postService.changePostToPublish(id, published);
        return ResponseEntity.noContent().build();
    }

}
