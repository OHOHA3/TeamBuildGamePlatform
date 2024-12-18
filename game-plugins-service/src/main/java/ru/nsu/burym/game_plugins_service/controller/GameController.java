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
@RequestMapping("/games")
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
            String containerId = gameInfoService.createGameInstance(createInfo.id(), createInfo.webSocketUrl());
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
