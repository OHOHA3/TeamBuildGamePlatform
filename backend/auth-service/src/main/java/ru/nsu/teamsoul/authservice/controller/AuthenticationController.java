package ru.nsu.teamsoul.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.teamsoul.authservice.controller.request.AuthenticationRequest;
import ru.nsu.teamsoul.authservice.controller.response.AuthenticationResponse;
import ru.nsu.teamsoul.authservice.controller.response.ValidationResponse;
import ru.nsu.teamsoul.authservice.service.AuthenticationService;

@RestController
@RequestMapping("/auth-service/api/v1")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid
            @RequestBody
            AuthenticationRequest request) {
        AuthenticationResponse response = service.authenticate(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/validate")
    public ResponseEntity<ValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Validating token with header: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authHeader.substring(7);

        ValidationResponse response = service.validateToken(token);
        return ResponseEntity.ok(response);
    }

}
