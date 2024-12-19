package com.example.service.dto;

public record RoomUsersRequest(
        Long gameId,
        Long roomId
) {
}
