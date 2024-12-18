package com.example.service.dto;

public record GameStatusRequest(
        Long gameId,
        String status
) {
}
