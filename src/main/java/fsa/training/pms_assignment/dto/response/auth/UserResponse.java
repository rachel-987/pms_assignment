package fsa.training.pms_assignment.dto.response.auth;

import fsa.training.pms_assignment.utils.enums;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String username;
    private enums.RoleName role;
    private boolean active;
}
