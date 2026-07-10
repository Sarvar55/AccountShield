package com.codems.accountshield.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.codems.accountshield.common.validation.UniqueEmail;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 255) @UniqueEmail String email,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
