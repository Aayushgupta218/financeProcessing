package com.example.financeProcessing.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Read the Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. If missing or doesn't start with "Bearer ", skip — let Spring Security
        //    handle it (will reject with 401 if the endpoint is protected)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Strip the "Bearer " prefix to get the raw token
        String token = authHeader.substring(7);

        // 4. Validate the token
        if (!jwtUtil.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Extract claims from the valid token
        Claims claims = jwtUtil.extractAllClaims(token);
        String userId = claims.getSubject();
        String role   = claims.get("role", String.class);

        // 6. Build Spring Security authentication object
        //    ROLE_ prefix is required by Spring Security for role-based checks
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,                                           // principal (user UUID)
                        null,                                             // credentials (not needed)
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)) // authority
                );

        // 7. Set in SecurityContext — Spring now knows WHO is making this request
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 8. Continue to the next filter / controller
        filterChain.doFilter(request, response);
    }
}