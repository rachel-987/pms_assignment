package fsa.training.pms_assignment.dto.request.auth;

import fsa.training.pms_assignment.utils.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private enums.RoleName role;
}
