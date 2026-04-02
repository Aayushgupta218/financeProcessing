package com.example.financeProcessing.auth;

import com.example.financeProcessing.auth.dto.AuthResponse;
import com.example.financeProcessing.auth.dto.LoginRequest;
import com.example.financeProcessing.auth.dto.RegisterRequest;
import com.example.financeProcessing.common.enums.Role;
import com.example.financeProcessing.user.User;
import com.example.financeProcessing.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        // 1. Check email is not already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // 2. Build and save the user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)  // default role for new registrations
                .isActive(true)
                .build();

        userRepository.save(user);

        // 3. Generate and return token immediately — no need to login separately
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole().name(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // 2. Check account is active
        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // 3. Verify password using BCrypt — compares raw vs hashed
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 4. Generate and return token
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole().name(), user.getName());
    }
}