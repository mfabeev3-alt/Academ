package ru.shatskikh.DTO;

import ru.shatskikh.entity.enums.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleResponseDto(

        Long id,
        String subjectName,
        String professorId,
        String professorContact,
        String room,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        boolean isMoved,
        LocalDate movedDate
) {}
