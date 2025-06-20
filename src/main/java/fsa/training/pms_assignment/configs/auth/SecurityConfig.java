package fsa.training.pms_assignment.configs.auth;

import fsa.training.pms_assignment.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity

public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",                  // Cho phép login
                                "/swagger-ui/**",            // Swagger UI static files
                                "/v3/api-docs/**",           // OpenAPI JSON
                                "/swagger-resources/**",     // Swagger internal resources
                                "/swagger-ui.html",          // Old UI fallback
                                "/webjars/**",  // JS/CSS resources
                                "swagger-ui"
                        ).permitAll()
                        // 🔓 Public endpoints cho POSTS
                        .requestMatchers(
                                "/api/posts/published",
                                "/api/posts/{id}",
                                "/api/posts",
                                "/api/posts/search",
                                "/api/posts/author",
                                "/api/posts/category/**"
                        ).permitAll()
                        // 🔓 Public endpoints cho CATEGORIES
                        .requestMatchers(
                                "/api/categories",
                                "/api/categories/active",
                                "/api/categories/{id}"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Role mà bạn set là 'ROLE_ADMIN' -> hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
