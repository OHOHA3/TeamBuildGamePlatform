package com.example.service.service;

import com.example.service.dto.*;
import com.example.service.repo.GameRepo;
import com.example.service.repo.GameRoomRepo;
import com.example.service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final GameRoomRepo gameRoomRepo;
    private final UserRepo userRepo;
    private final GameRepo gameRepo;

    public List<GameDto> getAllAvailableGames() {
        return List.of(
                new GameDto(1L, "game1", "v1", "this is description of game 1"),
                new GameDto(2L, "game2", "v1", "this is description of game 2"),
                new GameDto(3L, "game3", "v1", "this is description of game 3")
        );

    }

    public CreateGamesDto createGame() {
        //var createdRoom = gameRoomRepo.save(new GameRoom());

        return new CreateGamesDto(1L, "http://localhost:8080/games/game1");
    }

    public void userConnect(UserConnectRequest userConnectRequest) throws BadRequestException {
        var user = userRepo.findById(userConnectRequest.userId()).orElseThrow(() -> new BadRequestException("unknown user id"));
        user.setGameRoomId(userConnectRequest.roomId());
        userRepo.save(user);
    }

    public void userDisconnect(UserDisconnectRequest userConnectRequest) throws BadRequestException {
        var user = userRepo.findById(userConnectRequest.userId()).orElseThrow(() -> new BadRequestException("unknown user id"));
        user.setGameRoomId(null);
        userRepo.save(user);
    }

    public List<UserDto> getUsers() {
        return List.of(new UserDto(1L, "user1", 1L));
    }

    public void getGameStatus(GameStatusRequest gameStatusRequest) throws BadRequestException {
        var game = gameRepo.findById(gameStatusRequest.gameId()).orElseThrow(() -> new BadRequestException("unknown game id"));
        game.setStatus(gameStatusRequest.status());
        gameRepo.save(game);
    }
}
