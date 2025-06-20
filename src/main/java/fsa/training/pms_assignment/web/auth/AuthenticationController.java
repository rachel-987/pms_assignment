package fsa.training.pms_assignment.web.auth;

import fsa.training.pms_assignment.dto.request.auth.AuthRequest;
import fsa.training.pms_assignment.dto.response.auth.AuthResponse;
import fsa.training.pms_assignment.entity.auth.User;
import fsa.training.pms_assignment.repository.auth.UserRepository;
import fsa.training.pms_assignment.security.jwt.JwtService;
import fsa.training.pms_assignment.utils.enums;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    @PostMapping("/seed")
    public String seedUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole(enums.RoleName.ADMIN);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("editor").isEmpty()) {
            User editor = new User();
            editor.setUsername("editor");
            editor.setPassword(passwordEncoder.encode("123456"));
            editor.setRole(enums.RoleName.EDITOR);
            userRepository.save(editor);
        }

        return "Seeded admin & editor accounts successfully!";
    }
}
