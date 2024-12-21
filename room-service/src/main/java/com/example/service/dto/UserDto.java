package com.example.service.dto;

import java.time.LocalDateTime;

public record UserDto(
        String id,
        String username,
        String email,
        String team,
        String role,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {
}