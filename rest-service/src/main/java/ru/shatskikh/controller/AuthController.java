package ru.shatskikh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.AuthResponse;
import ru.shatskikh.DTO.LoginRequest;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Методы для входа в систему")
// @CrossOrigin(origins = "http://localhost:3000") // Если тестируешь с внешнего фронта, не забудь про CORS
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Вход через Telegram Mini App",
            description = "Принимает объект параметров (parsed initData), валидирует подпись и возвращает JWT токен"
    )
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request)
            throws AccessDeniedException, EntityNotFoundException {

        // Берем Map из поля data нашего DTO
        String token = authService.authenticate(request.data());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}