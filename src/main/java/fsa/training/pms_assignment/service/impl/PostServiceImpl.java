package fsa.training.pms_assignment.service.impl;

import fsa.training.pms_assignment.dto.request.Post.CreatePostRequest;
import fsa.training.pms_assignment.dto.request.Post.UpdatePostRequest;
import fsa.training.pms_assignment.dto.response.post.PostResponse;
import fsa.training.pms_assignment.entity.Category;
import fsa.training.pms_assignment.entity.Post;
import fsa.training.pms_assignment.mapper.PostMapper;
import fsa.training.pms_assignment.repository.CategoryRepository;
import fsa.training.pms_assignment.repository.PostRepository;
import fsa.training.pms_assignment.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.postMapper = postMapper;
    }

    public PostResponse createPost(CreatePostRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null)
            return null;

        Post post = postMapper.toEntity(request, category);
        postRepository.save(post);
        return postMapper.toResponse(post);
    }

    public PostResponse updatePost(UUID id, UpdatePostRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null)
            return null;

        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost == null)
            return null;

        postMapper.updateEntity(request, existingPost, category);
        postRepository.save(existingPost);
        return postMapper.toResponse(existingPost);
    }

    public PostResponse getPostById(UUID id) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost == null)
            return null;
        return postMapper.toResponse(existingPost);
}

    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }

    public List<PostResponse> getAllPublishedPosts() {
        return postRepository.findByPublishedTrue()
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public List<PostResponse> getPostsByCategory(UUID categoryId) {
        return postRepository.findByCategoryId(categoryId)
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public List<PostResponse> searchPostsByTitle(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public List<PostResponse> getPostsByAuthor(String author) {
        return postRepository.findByAuthor(author)
                .stream()
                .map(postMapper::toResponse )
                .toList();
    }

    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(postMapper::toResponse);
    }

    public void changePostToPublish(UUID id, boolean published) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost != null){
            existingPost.setPublished(published);
            existingPost.setPublishedAt(LocalDateTime.now());
            postRepository.save(existingPost);
        }
    }
}
