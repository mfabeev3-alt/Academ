package ru.shatskikh.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.EventRequestDto;
import ru.shatskikh.DTO.EventResponseDto;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.security.CustomUserDetails;
import ru.shatskikh.service.EventService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private final EventService eventService;

    @PostMapping("/add")
    @Operation(summary = "Добавление нового события")
    public ResponseEntity<EventResponseDto> add(
            @RequestBody @Valid EventRequestDto eventRequestDto,
            @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotCreatedException, EntityNotFoundException {

        Long groupId = principal.getGroupId();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.save(eventRequestDto, groupId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменение информации о событии")
    public ResponseEntity<EventResponseDto> edit(
            @RequestBody @Valid EventRequestDto eventRequestDto,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotCreatedException, EntityNotFoundException, AccessDeniedException {

        Long groupId = principal.getGroupId();

        return ResponseEntity.ok(eventService.edit(eventRequestDto, id, groupId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление события")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal)
            throws AccessDeniedException, EntityNotFoundException {

        Long groupId = principal.getGroupId();

        eventService.delete(id, groupId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Получение списка событий")
    public ResponseEntity<List<EventResponseDto>> getAllEvents(
            @AuthenticationPrincipal CustomUserDetails principal) {

        Long groupId = principal.getGroupId();

        return ResponseEntity.ok(eventService.findAll(groupId));
    }
}