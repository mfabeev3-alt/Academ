package ru.shatskikh.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.ScheduleRequestDto;
import ru.shatskikh.DTO.ScheduleResponseDto;
import ru.shatskikh.entity.enums.DayOfWeek;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.entity.exceptions.ScheduleConflictException;
import ru.shatskikh.security.JwtService;
import ru.shatskikh.service.ScheduleService;

import java.util.List;


@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<ScheduleResponseDto> add(@RequestBody @Valid ScheduleRequestDto scheduleRequestDto,
                                                   @RequestHeader("Authorization") String authHeader)
            throws EntityNotCreatedException, EntityNotFoundException, ScheduleConflictException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(scheduleRequestDto, groupId));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> edit(@RequestBody @Valid ScheduleRequestDto scheduleRequestDto,
                                                     @PathVariable Long id,
                                                     @RequestHeader("Authorization") String authHeader)
            throws EntityNotCreatedException, EntityNotFoundException, AccessDeniedException, ScheduleConflictException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.ok(scheduleService.edit(scheduleRequestDto, id, groupId));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader)
            throws EntityNotFoundException, AccessDeniedException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        scheduleService.delete(id, groupId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping()
    public ResponseEntity<List<ScheduleResponseDto>> index(@RequestHeader("Authorization") String authHeader,
                                                           @RequestParam(name = "week", required = false) Integer weekNumber,
                                                           @RequestParam(name = "day", required = false) DayOfWeek dayOfWeek)
            throws EntityNotFoundException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        if (weekNumber != null) {

            if(dayOfWeek != null){

                return ResponseEntity.ok(scheduleService.findScheduleForDay(groupId, dayOfWeek, weekNumber));
            }

            return ResponseEntity.ok(scheduleService.findScheduleForWeek(groupId, weekNumber));

            } else {

            return ResponseEntity.ok(scheduleService.findAll(groupId));

        }

    }

}
