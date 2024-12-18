package com.example.service.controller;

import com.example.service.dto.*;
import com.example.service.service.GameRoomService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void processMessage(@Payload GameStatusDto gameStatusDto) {
        //todo
    }

    @MessageMapping("/room/users")
    public void processMessage( ) {
        int id = 1;

        messagingTemplate.convertAndSendToUser(
                String.valueOf(id),"/queue/messages",
                new GameDto(1L, "", "", ""));
    }
}
