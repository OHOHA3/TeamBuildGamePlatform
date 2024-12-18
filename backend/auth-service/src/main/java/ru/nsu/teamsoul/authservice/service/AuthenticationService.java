package ru.nsu.teamsoul.authservice.service;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.nsu.teamsoul.authservice.controller.request.AuthenticationRequest;
import ru.nsu.teamsoul.authservice.controller.response.AuthenticationResponse;
import ru.nsu.teamsoul.authservice.controller.response.ValidationResponse;
import ru.nsu.teamsoul.authservice.exception.InvalidCredentialsException;
import ru.nsu.teamsoul.authservice.repository.UsersRepository;
import ru.nsu.teamsoul.authservice.repository.model.enums.Role;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService {
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        String identifier = request.getIdentifier();
        log.info("Authenticating user with identifier: {}", identifier);
        boolean isEmail = identifier.contains("@");

        var user = (isEmail ?
                usersRepository.findByEmail(identifier) :
                usersRepository.findByLogin(identifier))
                .orElseThrow(() -> {
                    log.warn("Users not found for input: {}", identifier);
                    return new InvalidCredentialsException("Invalid username/email or password.");
                });

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed for identifier: {} - {}", identifier, ex.getMessage());
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public ValidationResponse validateToken(String token) {
        log.info("Validating token: {}", token);

        return ValidationResponse.builder()
                .id(jwtService.extractUserId(token))
                .login(jwtService.extractLogin(token))
                .role(jwtService.extractRole(token))
                .build();
    }
}