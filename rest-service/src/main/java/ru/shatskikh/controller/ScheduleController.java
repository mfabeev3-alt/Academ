package ru.shatskikh.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.ScheduleResponseDto;
import ru.shatskikh.service.ScheduleService;
import ru.shatskikh.utils.ErrorMessageBuilder;


@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ErrorMessageBuilder errorMessageBuilder;
    private final ScheduleService scheduleService;

    @GetMapping("/get")
    public ScheduleResponseDto sendSchedule() {

        return null;
    }



}
