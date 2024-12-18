package ru.nsu.teamsoul.authservice.repository.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Role {
    hr("hr"),
    user("user"),
    admin("admin");

    private final String name;
    Role(String name) {
        this.name = name;
    }
    @JsonValue
    public String toValue() {
        return this.name;
    }
}
