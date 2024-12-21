package com.example.service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "User")
public class User implements Serializable {
    @Id
    private String id;
    private String name;
    private Long gameRoomId;

    public  User(String name, Long gameRoomId) {
        this.name = name;
        this.gameRoomId = gameRoomId;
    }

    @Override
    public String toString(){
        return "User{" + "id=" +id + '\''  + ", name =" + name + "}";
    }
}
