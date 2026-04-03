package com.example.financeProcessing.user.dto;

import com.example.financeProcessing.common.enums.Role;
import com.example.financeProcessing.user.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserResponse {
    private final UUID id;
    private final String name;
    private final String email;
    private final Role role;
    private final boolean active;

    // Static factory — converts entity to DTO cleanly
    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    private UserResponse(User user) {
        this.id     = user.getId();
        this.name   = user.getName();
        this.email  = user.getEmail();
        this.role   = user.getRole();
        this.active = user.isActive();
    }
}