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
        //  Check email is not already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Build and save the user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)  // default role
                .isActive(true)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole().name(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));


        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // Verify password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate and return token
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole().name(), user.getName());
    }
}