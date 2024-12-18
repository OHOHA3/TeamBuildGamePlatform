package com.example.service.dto;

public record RoomUsersRequest(
        Long userId,
        Long roomId
) {
}
