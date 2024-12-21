package com.example.service.dto;

public record UserResolveDto(
        Long id,
        String login,
        String role
) {
}
