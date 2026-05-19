package ru.shatskikh.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventRequestDto(

        @NotBlank(message = "Название мероприятия не может быть пустым")
        @Size(max = 255, message = "Название не должно превышать 255 символов")
        String name,
        String description,
        @NotNull(message = "Дата обязательна")
        @FutureOrPresent(message = "Событие не может быть в прошлом")
        LocalDateTime date
){}
