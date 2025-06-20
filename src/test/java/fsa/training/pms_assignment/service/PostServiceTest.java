package fsa.training.pms_assignment.service;

import fsa.training.pms_assignment.dto.request.Post.CreatePostRequest;
import fsa.training.pms_assignment.dto.request.Post.UpdatePostRequest;
import fsa.training.pms_assignment.dto.response.post.PostResponse;
import fsa.training.pms_assignment.entity.Category;
import fsa.training.pms_assignment.entity.Post;
import fsa.training.pms_assignment.mapper.PostMapper;
import fsa.training.pms_assignment.repository.CategoryRepository;
import fsa.training.pms_assignment.repository.PostRepository;
import fsa.training.pms_assignment.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private UUID postId;
    private UUID categoryId;
    private Post post;
    private Category category;
    private PostResponse response;

    @BeforeEach
    void init() {
        postId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = Category.builder().name("Tech").active(true).build();
        category.setId(categoryId);

        post = Post.builder().title("Title").content("Content").author("Author").category(category).published(true).build();
        post.setId(postId);

        response = PostResponse.builder().id(postId).title("Title").author("Author").published(true).build();
    }

    // ======= TEST 1: Create =======
    @Test
    void shouldCreatePostWhenCategoryExists() {
        CreatePostRequest request = CreatePostRequest.builder()
                .title("Title").categoryId(categoryId).build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(postMapper.toEntity(request, category)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(response);

        PostResponse result = postService.createPost(request);

        assertEquals("Title", result.getTitle());
        verify(postRepository).save(post);
    }

    @Test
    void shouldReturnNullWhenCreatePostCategoryNotFound() {
        CreatePostRequest request = CreatePostRequest.builder().categoryId(categoryId).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        PostResponse result = postService.createPost(request);

        assertNull(result);
    }

    // ======= TEST 2: Update =======
    @Test
    void shouldUpdatePostWhenExists() {
        UpdatePostRequest request = UpdatePostRequest.builder().title("New Title").categoryId(categoryId).build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(postMapper).updateEntity(request, post, category);
        when(postMapper.toResponse(post)).thenReturn(response);

        PostResponse result = postService.updatePost(postId, request);

        assertNotNull(result);
        verify(postRepository).save(post);
    }

    @Test
    void shouldReturnNullWhenUpdatePostCategoryNotFound() {
        UpdatePostRequest request = UpdatePostRequest.builder().categoryId(categoryId).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        PostResponse result = postService.updatePost(postId, request);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenUpdatePostNotFound() {
        UpdatePostRequest request = UpdatePostRequest.builder().categoryId(categoryId).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostResponse result = postService.updatePost(postId, request);

        assertNull(result);
    }

    // ======= TEST 3: Get By ID =======
    @Test
    void shouldReturnPostById() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        PostResponse result = postService.getPostById(postId);

        assertEquals(postId, result.getId());
    }

    @Test
    void shouldReturnNullWhenPostByIdNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostResponse result = postService.getPostById(postId);

        assertNull(result);
    }

    // ======= TEST 4: Delete =======
    @Test
    void shouldDeletePostById() {
        postService.deletePost(postId);
        verify(postRepository).deleteById(postId);
    }

    // ======= TEST 5: Change Publish Status =======
    @Test
    void shouldChangePostToPublished() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.changePostToPublish(postId, true);

        assertTrue(post.isPublished());
        verify(postRepository).save(post);
    }

    @Test
    void shouldNotChangeStatusWhenPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        postService.changePostToPublish(postId, true);

        verify(postRepository, never()).save(any());
    }

    // ======= TEST 6: Other Retrieval Methods =======
    @Test
    void shouldReturnAllPublishedPosts() {
        when(postRepository.findByPublishedTrue()).thenReturn(List.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        List<PostResponse> result = postService.getAllPublishedPosts();

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnPostsByCategory() {
        when(postRepository.findByCategoryId(categoryId)).thenReturn(List.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        List<PostResponse> result = postService.getPostsByCategory(categoryId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnPostsByTitleKeyword() {
        when(postRepository.findByTitleContainingIgnoreCase("title")).thenReturn(List.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        List<PostResponse> result = postService.searchPostsByTitle("title");

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnPostsByAuthor() {
        when(postRepository.findByAuthor("Author")).thenReturn(List.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        List<PostResponse> result = postService.getPostsByAuthor("Author");

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnAllPostsPaginated() {
        Page<Post> page = new PageImpl<>(List.of(post));
        Pageable pageable = PageRequest.of(0, 10);

        when(postRepository.findAll(pageable)).thenReturn(page);
        when(postMapper.toResponse(post)).thenReturn(response);

        Page<PostResponse> result = postService.getAllPosts(pageable);

        assertEquals(1, result.getTotalElements());
    }
}
