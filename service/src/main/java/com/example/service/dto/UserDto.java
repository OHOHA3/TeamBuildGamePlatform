package com.example.service.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        String team,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}