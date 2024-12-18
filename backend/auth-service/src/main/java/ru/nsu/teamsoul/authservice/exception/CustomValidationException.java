package ru.nsu.teamsoul.authservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class CustomValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public CustomValidationException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = errors;
    }
}