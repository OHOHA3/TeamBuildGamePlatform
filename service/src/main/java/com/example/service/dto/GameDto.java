package com.example.service.dto;

public record GameDto(
        Long id,
        String name,
        String version,
        String description
) {
}
