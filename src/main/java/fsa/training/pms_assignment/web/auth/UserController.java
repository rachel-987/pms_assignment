package fsa.training.pms_assignment.web.auth;

import fsa.training.pms_assignment.dto.request.auth.UserRequest;
import fsa.training.pms_assignment.dto.response.auth.UserResponse;
import fsa.training.pms_assignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}/inactive")
    public ResponseEntity<Void> inactivateUser(@PathVariable UUID id) {
        userService.inactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hardDeleteUsers(@PathVariable UUID id) {
        userService.hardDeleteUser(id);
        return ResponseEntity.ok().build();
    }
}
