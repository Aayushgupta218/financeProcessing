package com.example.financeProcessing.user.dto;

import com.example.financeProcessing.common.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateRoleRequest {
    @NotNull(message = "Role is required")
    private Role role;
}