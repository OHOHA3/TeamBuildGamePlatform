package com.example.service.service;

import com.example.service.dto.*;
import com.example.service.model.GameRoom;
import com.example.service.model.User;
import com.example.service.repo.GameRepo;
import com.example.service.repo.GameRoomRepo;
import com.example.service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final GameRoomRepo gameRoomRepo;
    private final UserRepo userRepo;
    private final GameRepo gameRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.game-plugin-service.host}")
    private String gamePluginServiceUrl;

    @Value("${app.auth-service.host}")
    private String authServiceUrl;

    @Value("${app.user-service.host}")
    private String userServiceUrl;

    public List<GameDto> getAllAvailableGames() {
        return List.of(new GameDto(1L, "game1", "v1", "description1"),
                new GameDto(2L, "game2", "v2", "description2"));
        /*ResponseEntity<List<GameDto>> response = restTemplate
                .exchange("http://" + gamePluginServiceUrl + ":8080/game-plugin/game-plugins-service/games", HttpMethod.GET, null,  new ParameterizedTypeReference<List<GameDto>>() {
                });
        return response.getBody();*/
    }

    public CreatedRoomDto createGame(String token) {
        /*var createdRoom = gameRoomRepo.save(new GameRoom());

//привязка
        ResponseEntity<CreateGamesDto> response = restTemplate
                .postForEntity("http://" + gamePluginServiceUrl + ":8080/game-plugin/game-plugins-service/games", new CreateGamesDto(createdRoom.getId(), "ws://localhost:8080/ws"),  CreateGamesDto.class);
        return response.getBody();*/
        return new CreatedRoomDto(1L);
    }

    public void userConnect(String token, UserConnectRequest userConnectRequest) throws BadRequestException {
        /*var user = userRepo.findById(userConnectRequest.userId()).orElseThrow(() -> new BadRequestException("unknown user id"));
        user.setGameRoomId(userConnectRequest.roomId());
        userRepo.save(user);*/
    }

    public void userDisconnect(String token) throws BadRequestException {
        /*var user = userRepo.findById(userConnectRequest.userId()).orElseThrow(() -> new BadRequestException("unknown user id"));
        user.setGameRoomId(null);
        userRepo.save(user);*/
    }

    public List<UserDto> getUsers() {
        ResponseEntity<List<UserDto>> response = restTemplate
                .exchange("http://" + gamePluginServiceUrl + ":8080/user-service/api/v1/users", HttpMethod.GET, null,  new ParameterizedTypeReference<List<UserDto>>() {
                });
        return response.getBody();
    }

    public void getGameStatus(GameStatusDto gameStatusDto) throws BadRequestException {
        var game = gameRepo.findById(gameStatusDto.gameId()).orElseThrow(() -> new BadRequestException("unknown game id"));
        game.setStatus(gameStatusDto.status());
        gameRepo.save(game);
    }

    public List<User> getRoomUsers(Long roomId) {
        List<User> roomUsers = new ArrayList<>();
        userRepo.findAll().forEach(roomUsers::add);

        return roomUsers.stream().filter(u -> Objects.equals(u.getGameRoomId(), roomId)).toList();
    }

    private UserDto getUserByToken(String token) {
        ResponseEntity<UserDto> response = restTemplate
                .getForEntity("http://" + authServiceUrl + ":8080/auth-service/api/v1/validate",  UserDto.class);
        return response.getBody();
    }
}