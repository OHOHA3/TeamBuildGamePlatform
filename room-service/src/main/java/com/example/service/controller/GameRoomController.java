package com.example.service.controller;

import com.example.service.dto.*;
import com.example.service.model.User;
import com.example.service.service.GameRoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/game-room-service/api/v1")
@RequiredArgsConstructor
@CrossOrigin
public class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping("/room/create")
    public CreatedRoomDto createRoom() {
        return gameRoomService.createRoom();
    }

    @GetMapping("/game/get-all")
    public List<GameDto> getAllAvailableGames() {
        return gameRoomService.getAllAvailableGames();
    }

    @PostMapping("/game/create")
    public UserConnectDto createGame(@RequestBody CreateGameRequest createGameRequest, HttpServletRequest request) throws BadRequestException {
        String token = request.getHeader("Authorization");
        return gameRoomService.createGame(createGameRequest, token);
    }

    @PostMapping("/user/connect")
    public UserConnectDto userConnect(@RequestBody UserConnectRequest userConnectRequest, HttpServletRequest request) throws BadRequestException {
        String token = request.getHeader("Authorization");
        return gameRoomService.userConnect(token, userConnectRequest);
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

        /*return List.of(new UserDto("1", "name3", "email3", "team3", "role3", LocalDateTime.now(), LocalDateTime.now()),
                new UserDto("2", "name2", "email2", "team2", "role2", LocalDateTime.now(), LocalDateTime.now()));*/
    }

}
