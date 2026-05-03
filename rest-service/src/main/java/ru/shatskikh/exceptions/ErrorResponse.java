package ru.shatskikh.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

public class ErrorResponse {
    private String message;
    private Instant timestep;

    public ErrorResponse(String message) {
        this.message = message;
        timestep = Instant.now();
    }
}
