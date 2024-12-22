package com.example.service.controller;

import com.example.service.dto.*;
import com.example.service.model.User;
import com.example.service.service.GameRoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/game-room-service/api/v1")
@RequiredArgsConstructor
@CrossOrigin
public class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping("/game/get-all")
    public List<GameDto> getAllAvailableGames() {
        return gameRoomService.getAllAvailableGames();
    }

    @GetMapping("/game/create")
    public CreatedRoomDto createGame(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return gameRoomService.createGame(token);
    }

    @PostMapping("/user/connect")
    public void userConnect(@RequestBody UserConnectRequest userConnectRequest, HttpServletRequest request) throws BadRequestException {
        String token = request.getHeader("Authorization");
        gameRoomService.userConnect(token, userConnectRequest);
    }

    @PostMapping("/user/disconnect")
    public void userDisconnect(HttpServletRequest request) throws BadRequestException {
        String token = request.getHeader("Authorization");
        gameRoomService.userDisconnect(token);
    }

    @PostMapping("/game/status")
    public void gameStatus(@RequestBody GameStatusDto gameStatusDto) throws BadRequestException {
        gameRoomService.getGameStatus(gameStatusDto);
    }

    @PostMapping("/room/users")
    public List<UserDto> getRoomUsers(@RequestBody RoomUsersRequest roomUsersRequest) {
        List<UserDto> users = gameRoomService.getUsers();
        List<User> roomUsers = gameRoomService.getRoomUsers(roomUsersRequest.roomId());

        return roomUsers.stream().map(u -> users.stream().filter(apiU -> Objects.equals(apiU.id(), String.valueOf(u.getId()))).findAny().orElse(null)).toList();
    }

}
