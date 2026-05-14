package ru.shatskikh.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

@Schema(description = "Запрос на авторизацию через Telegram")
public record LoginRequest(
        @Schema(description = "Параметры из Telegram (user, hash, auth_date и т.д.)")
        Map<String, String> data
) {}