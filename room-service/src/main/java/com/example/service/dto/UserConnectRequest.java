package com.example.service.dto;

public record UserConnectRequest(
        Long roomId,
        Long userId
) {
}
