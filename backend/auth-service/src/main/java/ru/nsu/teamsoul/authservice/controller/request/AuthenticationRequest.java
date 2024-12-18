package ru.nsu.teamsoul.authservice.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "Identifier cannot be blank")
    @Size(min = 3, max = 50, message = "Identifier must be between 3 and 50 characters")
    private String identifier;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 24, message = "Username or password is invalid")
    private String password;
}
