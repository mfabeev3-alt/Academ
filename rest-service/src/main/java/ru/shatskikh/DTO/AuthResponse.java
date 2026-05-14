package ru.shatskikh.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT токеном")
public record AuthResponse(
        @Schema(description = "JWT токен для доступа к API")
        String token
) {}