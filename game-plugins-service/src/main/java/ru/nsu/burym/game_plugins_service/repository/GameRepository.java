package ru.nsu.burym.game_plugins_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.burym.game_plugins_service.entity.Game;

public interface GameRepository extends JpaRepository<Game, Integer> {

}
