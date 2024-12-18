package com.example.service.repo;

import com.example.service.model.GameRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameRoomRepo extends CrudRepository<GameRoom,Long> {

}
