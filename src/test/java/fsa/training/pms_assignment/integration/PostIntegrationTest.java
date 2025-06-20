package fsa.training.pms_assignment.integration;

import fsa.training.pms_assignment.dto.request.Post.CreatePostRequest;
import fsa.training.pms_assignment.dto.request.Post.UpdatePostRequest;
import fsa.training.pms_assignment.dto.request.auth.AuthRequest;
import fsa.training.pms_assignment.dto.request.category.CreateCategoryRequest;
import fsa.training.pms_assignment.dto.response.auth.AuthResponse;
import fsa.training.pms_assignment.dto.response.category.CategoryResponse;
import fsa.training.pms_assignment.dto.response.post.PostResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/posts";
    }
    private String getCategoryUrl() {
        return "http://localhost:" + port + "/api/categories";
    }
    private UUID categoryId;
    
    @BeforeEach
    void init(){
        authenticate();
        // Create Category
        categoryId = createCategory();
    }

    @AfterEach
    void tearDown(){
        deleteCategory(categoryId);
    }

    private void authenticate() {
        AuthRequest request = new AuthRequest("admin", "123456");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                request,
                AuthResponse.class
        );

        String token = response.getBody().getToken();

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
    }

    private UUID createCategory(){
        CreateCategoryRequest createCategoryReq = new CreateCategoryRequest();
        createCategoryReq.setName("IT Category");
        createCategoryReq.setDescription("Category for IT test");

        HttpEntity<CreateCategoryRequest> createCategoryEntity = new HttpEntity<>(createCategoryReq, headers);

        ResponseEntity<CategoryResponse> createCategoryResp = restTemplate.postForEntity(
                getCategoryUrl(), createCategoryEntity, CategoryResponse.class);
        return createCategoryResp.getBody().getId();
    }

    private void deleteCategory(UUID categoryId){
        // Delete
        if(categoryId != null) {
            ResponseEntity<Void> deleteResp = restTemplate.exchange(
                    getCategoryUrl() + "/" + categoryId,
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class
            );
            deleteResp.getStatusCode();
        }
    }

    @Test
    void testCreateUpdateGetDeletePost() {
        // Tạo bài viết
        CreatePostRequest createPostReq = new CreatePostRequest();
        createPostReq.setTitle("Integration Test Post");
        createPostReq.setContent("This is test content");
        createPostReq.setCategoryId(categoryId);
        createPostReq.setAuthor("admin");

        ResponseEntity<PostResponse> createPostResp = restTemplate.postForEntity(
                getBaseUrl(),
                new HttpEntity<>(createPostReq, headers),
                PostResponse.class
        );

        assertThat(createPostResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        UUID postId = createPostResp.getBody().getId();

        // Cập nhật
        UpdatePostRequest updateReq = new UpdatePostRequest();
        updateReq.setTitle("Updated Title");
        updateReq.setContent("Updated content");
        updateReq.setAuthor("Updated author");
        updateReq.setCategoryId(categoryId);

        ResponseEntity<PostResponse> updateResp = restTemplate.exchange(
                getBaseUrl() + "/" + postId,
                HttpMethod.PUT,
                new HttpEntity<>(updateReq, headers),
                PostResponse.class
        );

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Xem chi tiết
        ResponseEntity<PostResponse> getResp = restTemplate.exchange(
                getBaseUrl() + "/" + postId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PostResponse.class
        );

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Xoá
        ResponseEntity<Void> deleteResp = restTemplate.exchange(
                getBaseUrl() + "/" + postId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testGetPublishedPosts() {
        ResponseEntity<List<PostResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/published",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PostResponse>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}