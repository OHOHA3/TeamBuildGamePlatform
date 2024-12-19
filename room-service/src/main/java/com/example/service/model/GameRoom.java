package com.example.service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "GameRoom")
public class GameRoom implements Serializable {
    @Id
    private Long id;
    private Long gameId;

    public GameRoom(Long gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString(){
        return "GameRoom{" + "id=" +id + "}";
    }
}