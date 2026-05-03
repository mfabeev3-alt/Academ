package ru.shatskikh.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.shatskikh.entity.enums.DayOfWeek;

import java.time.LocalTime;

public record ScheduleRequestDto(
        @NotNull(message = "ID предмета не может быть пустым")
        @Positive(message = "Неверный ID предмета")
        Long subjectId,

        @NotNull(message = "ID преподавателя не может быть пустым")
        @Positive(message = "Неверный ID преподавателя")
        Long professorId,

        @NotBlank(message = "Аудитория не может быть пустой")
        String room,

        @NotNull(message = "День недели не может быть пустым")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Время начала не может быть пустым")
        LocalTime startTime,

        @NotNull(message = "Время окончания не может быть пустым")
        LocalTime endTime
) {}
