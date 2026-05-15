package ru.shatskikh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.ScheduleRequestDto;
import ru.shatskikh.DTO.ScheduleResponseDto;
import ru.shatskikh.entity.enums.DayOfWeek;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.entity.exceptions.ScheduleConflictException;
import ru.shatskikh.security.CustomUserDetails;
import ru.shatskikh.security.JwtService;
import ru.shatskikh.service.ScheduleService;

import java.util.List;


@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Tag(name = "Расписание")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;

    @PostMapping("/add")
    @Operation(summary = "Сделать новую запись занятия")
    public ResponseEntity<ScheduleResponseDto> add(@RequestBody @Valid ScheduleRequestDto scheduleRequestDto,
                                                   @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotCreatedException, EntityNotFoundException, ScheduleConflictException {

        Long groupId = principal.getGroupId();

        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(scheduleRequestDto, groupId));

    }

    @PatchMapping("/{id}")
    @Operation(summary = "Редактирование записи")
    public ResponseEntity<ScheduleResponseDto> edit(@RequestBody @Valid ScheduleRequestDto scheduleRequestDto,
                                                     @PathVariable Long id,
                                                    @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotCreatedException, EntityNotFoundException, AccessDeniedException, ScheduleConflictException {

        Long groupId = principal.getGroupId();
        return ResponseEntity.ok(scheduleService.edit(scheduleRequestDto, id, groupId));

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление записи о занятии")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotFoundException, AccessDeniedException {

        Long groupId = principal.getGroupId();
        scheduleService.delete(id, groupId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    @Operation(summary = "Удаление всех записей о занятиях конкретной группы")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails principal)
            throws AccessDeniedException {

        Long groupId = principal.getGroupId();

        scheduleService.deleteAllSchedule(groupId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Operation(summary = "Получение всех записей за период времени")
    public ResponseEntity<List<ScheduleResponseDto>> getAllScheduleItems(@AuthenticationPrincipal CustomUserDetails principal,
                                                           @RequestParam(name = "week", required = false) Integer weekNumber,
                                                           @RequestParam(name = "day", required = false) DayOfWeek dayOfWeek)
            throws EntityNotFoundException {

        Long groupId = principal.getGroupId();;

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
