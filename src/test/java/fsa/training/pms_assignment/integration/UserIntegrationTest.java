package fsa.training.pms_assignment.integration;

import fsa.training.pms_assignment.dto.request.auth.AuthRequest;
import fsa.training.pms_assignment.dto.request.auth.UserRequest;
import fsa.training.pms_assignment.dto.response.auth.AuthResponse;
import fsa.training.pms_assignment.dto.response.auth.UserResponse;
import fsa.training.pms_assignment.utils.enums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String token;
    private HttpHeaders headers;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/admin/users";
    }

    private String getLoginUrl() {
        return "http://localhost:" + port + "/auth/login";
    }

    @BeforeEach
    void setup() {
        // Login with admin account in DB
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                getLoginUrl(),
                loginRequest,
                AuthResponse.class
        );


        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        token = loginResponse.getBody().getToken();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
    }

    @Test
    void testCreateUpdateInactivateUser() {
        // 1. Tạo user
        UserRequest createRequest = new UserRequest();
        createRequest.setUsername("integration_user");
        createRequest.setPassword("12345678");
        createRequest.setRole(enums.RoleName.EDITOR);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequest> createEntity = new HttpEntity<>(createRequest, headers);

        ResponseEntity<UserResponse> createResponse = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                createEntity,
                UserResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserResponse createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("integration_user");

        UUID userId = createdUser.getId();

        // 2. Update user
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("integration_updated");
        updateRequest.setPassword("newpass");
        updateRequest.setRole(enums.RoleName.EDITOR);

        HttpEntity<UserRequest> updateEntity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<UserResponse> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/" + userId,
                HttpMethod.PUT,
                updateEntity,
                UserResponse.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserResponse updatedUser = updateResponse.getBody();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getUsername()).isEqualTo("integration_updated");

        // 3. Inactive user
        ResponseEntity<Void> inactiveResponse = restTemplate.exchange(
                getBaseUrl() + "/" + userId + "/inactive",
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(inactiveResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 4. Delete User
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/" + userId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
