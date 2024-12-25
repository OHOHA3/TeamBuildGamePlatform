package ru.nsu.burym.game_plugins_service.controller;

import com.github.dockerjava.api.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.burym.game_plugins_service.jsonmodel.request.CreateInfo;
import ru.nsu.burym.game_plugins_service.jsonmodel.response.GameOutput;
import ru.nsu.burym.game_plugins_service.service.GameInfoService;

import java.util.List;

@RestController
@RequestMapping("/game-plugins-service/api/v1/games")
@CrossOrigin
public class GameController {

    private final GameInfoService gameInfoService;

    @Autowired
    public GameController(GameInfoService gameInfoService) {
        this.gameInfoService = gameInfoService;
    }

    @GetMapping()
    public List<GameOutput> getGamesList() {
        return gameInfoService.getGamesList();
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGameInstance(@RequestBody CreateInfo createInfo) {
        try {
            System.out.println("Request: roomId: " + createInfo.roomId());
            String containerId = gameInfoService.createGameInstance(createInfo.gameId(), createInfo.roomId());
            return ResponseEntity.ok(containerId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stop/{containerId}")
    public ResponseEntity<String> stop(@PathVariable String containerId) {
        try {
            gameInfoService.stopGameInstance(containerId);
            return ResponseEntity.ok("Success");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
