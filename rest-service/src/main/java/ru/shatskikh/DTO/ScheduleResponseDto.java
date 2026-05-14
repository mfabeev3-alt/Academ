package ru.shatskikh.DTO;

import ru.shatskikh.entity.enums.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record ScheduleResponseDto(

        Long id,
        String subjectName,
        String professorName,
        String professorContact,
        String room,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Set<Integer> activeWeeks

) {}
