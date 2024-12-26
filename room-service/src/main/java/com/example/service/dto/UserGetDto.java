package com.example.service.dto;

import java.time.LocalDateTime;

public record UserGetDto(
        LocalDateTime created_at,
        String email,
        String id,
        String login,
        String role,
        String status,
        LocalDateTime updated_at
) {
}