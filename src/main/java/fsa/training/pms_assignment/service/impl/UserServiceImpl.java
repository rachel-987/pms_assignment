package fsa.training.pms_assignment.service.impl;

import fsa.training.pms_assignment.dto.request.auth.UserRequest;
import fsa.training.pms_assignment.dto.response.auth.UserResponse;
import fsa.training.pms_assignment.entity.auth.User;
import fsa.training.pms_assignment.repository.auth.UserRepository;
import fsa.training.pms_assignment.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);

        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(request.getRole());

        userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public void hardDeleteUser(UUID id) {
        userRepository.deleteById(id);
    }


    @Override
    public void inactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setActive(user.isIsActive());
        return response;
    }

}
