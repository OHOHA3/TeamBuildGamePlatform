package ru.nsu.teamsoul.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.teamsoul.authservice.repository.model.Users;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByLogin(String login);
    Optional<Users> findByEmail(String email);
}
