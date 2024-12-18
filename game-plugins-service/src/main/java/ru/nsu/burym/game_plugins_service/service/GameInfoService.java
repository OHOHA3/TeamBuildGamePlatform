package ru.nsu.burym.game_plugins_service.service;

import com.github.dockerjava.api.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.burym.game_plugins_service.docker.DockerContainerHandler;
import ru.nsu.burym.game_plugins_service.entity.Game;
import ru.nsu.burym.game_plugins_service.repository.GameRepository;
import ru.nsu.burym.game_plugins_service.jsonmodel.response.GameOutput;

import java.util.List;

@Service
public class GameInfoService {

    static class GameMapper {
        static GameOutput mapToGameOutput(Game game) {
            return new GameOutput(game.getId(), game.getName(), game.getVersion(), game.getDescription());
        }
    }

    private final GameRepository gameRepository;

    private final DockerContainerHandler dockerContainerHandler;

    @Autowired
    public GameInfoService(GameRepository gameRepository, DockerContainerHandler dockerContainerHandler) {
        this.gameRepository = gameRepository;
        this.dockerContainerHandler = dockerContainerHandler;
    }

    public List<GameOutput> getGamesList() {
        return gameRepository.findAll().stream().map(GameMapper::mapToGameOutput).toList();
    }

    public String createGameInstance(int id, String webSocketUrl) throws IllegalArgumentException {
        if (webSocketUrl == null) {
            throw new IllegalArgumentException("webSocketUrl is null");
        }
        String imageName = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Game id " + id + " not found"))
                .getDockerImageName();
        return dockerContainerHandler.createContainer(imageName, webSocketUrl);
    }

    public void stopGameInstance(String containerId) throws NotFoundException {
        dockerContainerHandler.stopAndRemoveContainer(containerId);
    }
}
