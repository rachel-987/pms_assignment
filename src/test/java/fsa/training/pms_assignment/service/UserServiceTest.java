package fsa.training.pms_assignment.service;

import fsa.training.pms_assignment.dto.request.auth.UserRequest;
import fsa.training.pms_assignment.dto.response.auth.UserResponse;
import fsa.training.pms_assignment.entity.auth.User;
import fsa.training.pms_assignment.repository.auth.UserRepository;
import fsa.training.pms_assignment.service.impl.UserServiceImpl;
import fsa.training.pms_assignment.utils.enums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;
    private UserRequest request;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        request = new UserRequest("testuser", "123456", enums.RoleName.EDITOR);
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(enums.RoleName.EDITOR);
        user.setIsActive(true);
    }

    // ========== CREATE ==========
    @Test
    void shouldCreateUserWhenUsernameNotExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.createUser(request);

        assertEquals("testuser", response.getUsername());
        assertEquals(enums.RoleName.EDITOR, response.getRole());
        assertTrue(response.isActive());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.createUser(request);
        });

        assertEquals("Username already exists", ex.getMessage());
    }

    // ========== UPDATE ==========
    @Test
    void shouldUpdateUserWithNewPassword() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("123456")).thenReturn("encodedNewPassword");

        UserRequest updateRequest = new UserRequest("updatedUser", "123456", enums.RoleName.ADMIN);

        UserResponse response = userService.updateUser(userId, updateRequest);

        assertEquals("updatedUser", response.getUsername());
        assertEquals(enums.RoleName.ADMIN, response.getRole());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateUserWithoutChangingPassword() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserRequest updateRequest = new UserRequest("updatedUser", "", enums.RoleName.ADMIN);

        UserResponse response = userService.updateUser(userId, updateRequest);

        assertEquals("updatedUser", response.getUsername());
        assertEquals(enums.RoleName.ADMIN, response.getRole());

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, request);
        });

        assertEquals("User not found", ex.getMessage());
    }

    // ========== INACTIVATE ==========
    @Test
    void shouldInactivateUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.inactivateUser(userId);

        assertFalse(user.isIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenInactivateUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.inactivateUser(userId);
        });

        assertEquals("User not found", ex.getMessage());
    }
}
