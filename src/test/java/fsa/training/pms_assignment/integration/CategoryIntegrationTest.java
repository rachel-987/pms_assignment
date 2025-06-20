package fsa.training.pms_assignment.integration;


import fsa.training.pms_assignment.dto.request.auth.AuthRequest;
import fsa.training.pms_assignment.dto.request.category.CreateCategoryRequest;
import fsa.training.pms_assignment.dto.request.category.UpdateCategoryRequest;
import fsa.training.pms_assignment.dto.response.auth.AuthResponse;
import fsa.training.pms_assignment.dto.response.category.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/categories";
    }

    @BeforeEach
    void setup() {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                loginRequest,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        String token = response.getBody().getToken();

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
    }

    @Test
    void testCreateUpdateDeleteCategory() {
        // Create
        CreateCategoryRequest createReq = new CreateCategoryRequest();
        createReq.setName("IT Category");
        createReq.setDescription("Category for IT test");

        HttpEntity<CreateCategoryRequest> createEntity = new HttpEntity<>(createReq, headers);

        ResponseEntity<CategoryResponse> createResp = restTemplate.postForEntity(
                getBaseUrl(), createEntity, CategoryResponse.class);

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResp.getBody()).isNotNull();
        UUID categoryId = createResp.getBody().getId();

        // Update
        UpdateCategoryRequest updateReq = new UpdateCategoryRequest();
        updateReq.setName("Updated IT Category");
        updateReq.setDescription("Updated description");

        HttpEntity<UpdateCategoryRequest> updateEntity = new HttpEntity<>(updateReq, headers);
        ResponseEntity<CategoryResponse> updateResp = restTemplate.exchange(
                getBaseUrl() + "/" + categoryId,
                HttpMethod.PUT,
                updateEntity,
                CategoryResponse.class
        );

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody().getName()).isEqualTo("Updated IT Category");

        // Toggle active
        ResponseEntity<Void> toggleResp = restTemplate.exchange(
                getBaseUrl() + "/" + categoryId + "/set-active?active=false",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(toggleResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Delete
        ResponseEntity<Void> deleteResp = restTemplate.exchange(
                getBaseUrl() + "/" + categoryId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
