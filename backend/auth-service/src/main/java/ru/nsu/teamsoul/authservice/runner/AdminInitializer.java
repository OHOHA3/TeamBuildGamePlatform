package ru.nsu.teamsoul.authservice.runner;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.nsu.teamsoul.authservice.repository.UsersRepository;
import ru.nsu.teamsoul.authservice.repository.model.Users;
import ru.nsu.teamsoul.authservice.repository.model.enums.Role;
import ru.nsu.teamsoul.authservice.repository.model.enums.Status;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.login}")
    private String adminLogin;

    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean adminExists = usersRepository.findByLogin(adminLogin).isPresent();

        if (!adminExists) {
            // Создаем нового администратора
            Users adminUsers = Users.builder()
                    .login(adminLogin)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.admin)
                    .status(Status.active)
                    .build();

            usersRepository.save(adminUsers);
            log.info("Admin user created: {}", adminLogin);
        } else {
            log.info("Admin user already exists. Skipping creation.");
        }
    }
}
