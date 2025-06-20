package fsa.training.pms_assignment.service;

import fsa.training.pms_assignment.dto.request.auth.UserRequest;
import fsa.training.pms_assignment.dto.response.auth.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(UUID id, UserRequest request);
    void hardDeleteUser(UUID id);
    void inactivateUser(UUID id);
}
