package ru.shatskikh.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.shatskikh.entity.enums.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import ru.shatskikh.entity.enums.DayOfWeek;

import java.time.LocalTime;
import java.util.Set;

@Schema(description = "Запрос на создание/редактирование записи в расписании")
public record ScheduleRequestDto(

        @Schema(description = "ID предмета", example = "1")
        @NotNull(message = "ID предмета не может быть пустым.")
        @Positive(message = "Неверный ID предмета.")
        Long subjectId,

        @Schema(description = "ID преподавателя", example = "5")
        @NotNull(message = "ID преподавателя не может быть пустым.")
        @Positive(message = "Неверный ID преподавателя.")
        Long professorId,

        @Schema(description = "Номер аудитории", example = "402-б")
        @NotBlank(message = "Аудитория не может быть пустой.")
        String room,

        @Schema(description = "День недели", example = "MONDAY")
        @NotNull(message = "День недели не может быть пустым.")
        DayOfWeek dayOfWeek,

        @Schema(description = "Время начала занятия", example = "09:00:00", type = "string", pattern = "HH:mm")
        @NotNull(message = "Время начала не может быть пустым.")
        LocalTime startTime,

        @Schema(description = "Время окончания занятия", example = "10:30:00", type = "string", pattern = "HH:mm")
        @NotNull(message = "Время окончания не может быть пустым.")
        LocalTime endTime,

        @Schema(description = "Список учебных недель, в которые проводится занятие", example = "[1, 2, 3, 4]")
        @NotEmpty(message = "Нужно указать хотя бы одну активную неделю.")
        Set<Integer> activeWeeks
) {}
