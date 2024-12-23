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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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

    private String gameUrl = "http://194.226.49.153:3000";

    private int port = 8080;

    public List<GameDto> getAllAvailableGames() {
        /*return List.of(new GameDto(1L, "game1", "v1", "description1"),
                new GameDto(2L, "game2", "v2", "description2"));*/
        ResponseEntity<List<GameDto>> response = restTemplate
                .exchange("http://" + gamePluginServiceUrl + ":"+port+"/game-plugins-service/api/v1/games", HttpMethod.GET, null,  new ParameterizedTypeReference<List<GameDto>>() {
                });
        return response.getBody();
    }

    public UserConnectDto createGame(CreateGameRequest createGameRequest, String token) throws BadRequestException {
        var room = gameRoomRepo.findById(createGameRequest.roomId()).orElseThrow(() -> new BadRequestException("room not found"));
        var userDto = getUserByToken(token);
        var userOptional = userRepo.findById(userDto.id());
        if(userOptional.isEmpty()) {
            var user = new User(userDto.id(), userDto.username(), room.getId());
            user.setGameRoomId(room.getId());
            userRepo.save(user);
        } else {
            var user = userOptional.get();
            user.setGameRoomId(room.getId());
            userRepo.save(user);
        }

        restTemplate
                .postForEntity("http://" + gamePluginServiceUrl + ":"+port+"/game-plugins-service/api/v1/games/create", new CreateGameDto(createGameRequest.id(), createGameRequest.roomId()),  String.class);

        return new UserConnectDto(gameUrl);
    }

    public UserConnectDto userConnect(String token, UserConnectRequest userConnectRequest) throws BadRequestException {
        /*var userDto = getUserByToken(token);
        var user = userRepo.findById(userDto.id()).orElseThrow(() -> new BadRequestException("unknown user id"));
        user.setGameRoomId(userConnectRequest.roomId());
        userRepo.save(user);*/

        var userDto = getUserByToken(token);
        var userOptional = userRepo.findById(userDto.id());
        if(userOptional.isEmpty()) {
            var user = new User(userDto.id(), userDto.username(), userConnectRequest.roomId());
            user.setGameRoomId(userConnectRequest.roomId());
            userRepo.save(user);
        } else {
            var user = userOptional.get();
            user.setGameRoomId(userConnectRequest.roomId());
            userRepo.save(user);
        }

        return new UserConnectDto(gameUrl);
    }

    public void userDisconnect(String token) throws BadRequestException {
        var userDto = getUserByToken(token);
        var userOptional = userRepo.findById(userDto.id());
        if(userOptional.isEmpty()) {
            var user = new User(userDto.id(), userDto.username(), null);
            user.setGameRoomId(null);
            userRepo.save(user);
        } else {
            var user = userOptional.get();
            user.setGameRoomId(null);
            userRepo.save(user);
        }
    }

    public List<UserDto> getUsers() {
        ResponseEntity<List<UserDto>> response = restTemplate
                .exchange("http://" + userServiceUrl + ":"+port+"/user-service/api/v1/users", HttpMethod.GET, null,  new ParameterizedTypeReference<List<UserDto>>() {
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
        /*ResponseEntity<UserDto> response = restTemplate
                .getForEntity("http://" + authServiceUrl + ":3222w3w/auth-service/api/v1/validate",  UserDto.class);*/

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDto> response =
                restTemplate.exchange("http://" + authServiceUrl + ":"+port+"/auth-service/api/v1/validate", HttpMethod.GET, entity, UserDto.class);
        return response.getBody();
    }

    public CreatedRoomDto createRoom() {
        var id = ThreadLocalRandom.current().nextLong(0, 99999);
//        System.out.println(id);
        var createdRoom = gameRoomRepo.save(new GameRoom(id));

        return new CreatedRoomDto(createdRoom.getId());
    }
}