package ru.nsu.teamsoul.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class CustomHttpException extends RuntimeException {
    private final HttpStatus status;
    public CustomHttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
