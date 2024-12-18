package ru.nsu.teamsoul.authservice.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("role")
    private String role;
}
