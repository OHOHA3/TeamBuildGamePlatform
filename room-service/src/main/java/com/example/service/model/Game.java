package com.example.service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Game")
public class Game implements Serializable {
    @Id
    private Long id;
    private String name;
    private String status;

    public Game(String name, String status) {
        this.name = name;
        this.status = status;
    }

    @Override
    public String toString(){
        return "Game{" + "id=" +id + '\''  + ", name =" + name + "}";
    }
}
