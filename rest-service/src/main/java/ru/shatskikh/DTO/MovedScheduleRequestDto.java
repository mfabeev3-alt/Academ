package ru.shatskikh.DTO;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MovedScheduleRequestDto(

        @NotNull(message = "Флаг переноса обязателен")
        boolean isMoved,
        LocalDate moveDate

) {}
