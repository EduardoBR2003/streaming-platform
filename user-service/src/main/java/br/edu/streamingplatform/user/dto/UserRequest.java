package br.edu.streamingplatform.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must have at most 120 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 160, message = "email must have at most 160 characters")
        String email,

        @NotBlank(message = "plan is required")
        @Size(max = 50, message = "plan must have at most 50 characters")
        String plan
) {
}
