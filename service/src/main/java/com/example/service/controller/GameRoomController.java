package com.example.service.controller;

import com.example.service.dto.*;
import com.example.service.model.User;
import com.example.service.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/game-room-service/api/v1")
@RequiredArgsConstructor
public class GameRoomController {
    private final GameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/game/get-all")
    public List<GameDto> getAllAvailableGames() {
        return gameRoomService.getAllAvailableGames();
    }

    @GetMapping("/game/create")
    public CreateGamesDto createGame() {
        return gameRoomService.createGame();
    }

    @PostMapping("/user/connect")
    public void userConnect(@RequestBody UserConnectRequest userConnectRequest) throws BadRequestException {
        gameRoomService.userConnect(userConnectRequest);
    }

    @PostMapping("/user/disconnect")
    public void userDisconnect(@RequestBody UserDisconnectRequest userConnectRequest) throws BadRequestException {
        gameRoomService.userDisconnect(userConnectRequest);
    }

    @MessageMapping("/game/status")
    public void getGameStatus(@Payload GameStatusDto gameStatusDto) throws BadRequestException {
        gameRoomService.getGameStatus(gameStatusDto);
    }

    @MessageMapping("/room/users")
    public void getUsers(@Payload RoomUsersRequest roomUsersRequest) {
        List<UserDto> users = gameRoomService.getUsers();
        List<User> roomUsers = gameRoomService.getRoomUsers(roomUsersRequest.roomId());

        var updatedList = roomUsers.stream().map(u -> users.stream().filter(apiU -> Objects.equals(apiU.id(), u.getId())).findAny().orElse(null)).toList();

        messagingTemplate.convertAndSendToUser(
                String.valueOf(roomUsersRequest.userId()),"/queue/messages",
                updatedList);
    }
}
