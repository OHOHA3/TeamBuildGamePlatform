package ru.nsu.teamsoul.authservice.repository.model.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Status {
    active("active"),
    blocked("blocked");

    private final String name;
    Status(String name) {
        this.name = name;
    }
    @JsonValue
    public String toValue() {
        return this.name;
    }
}
