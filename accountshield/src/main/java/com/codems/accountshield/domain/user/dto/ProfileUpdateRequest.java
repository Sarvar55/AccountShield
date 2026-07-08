package com.codems.accountshield.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 150) String bio
) {
}
