package ru.nsu.burym.game_plugins_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Game {
    @Id
    private Integer id;

    private String name;

    private String version;

    private String description;

    private String dockerImageName;
}
