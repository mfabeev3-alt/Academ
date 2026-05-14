package ru.shatskikh.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.ProfessorRequestDto;
import ru.shatskikh.DTO.ProfessorResponseDto;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.mappers.ProfessorMapper;
import ru.shatskikh.security.CustomUserDetails;
import ru.shatskikh.security.JwtService;
import ru.shatskikh.service.ProfessorService;

import java.util.List;

@RestController
@RequestMapping("/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;
    private final ProfessorMapper professorMapper;
    private final JwtService jwtService;


    @PostMapping("/add")
    @Operation(summary = "Добавление нового преподавателя")
    public ResponseEntity<ProfessorResponseDto> add(@RequestBody @Valid ProfessorRequestDto professorRequestDto,
                                                    @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotCreatedException, EntityNotFoundException {

        Long groupId = principal.getGroupId();

        return ResponseEntity.status(HttpStatus.CREATED).body(professorService.save(professorRequestDto, groupId));

    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменение информации о преподавателя")
    public ResponseEntity<ProfessorResponseDto> edit(@RequestBody @Valid ProfessorRequestDto professorRequestDto,
                                                   @PathVariable Long id,
                                                     @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotCreatedException, EntityNotFoundException, AccessDeniedException {

        Long groupId = principal.getGroupId();

        return ResponseEntity.ok(professorService.edit(professorRequestDto, id, groupId));

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление преподавателя")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotFoundException, AccessDeniedException {

        Long groupId = principal.getGroupId();
        professorService.delete(id, groupId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Получение списка преподавателей")
    public ResponseEntity<List<ProfessorResponseDto>> getAllProfessors(@AuthenticationPrincipal CustomUserDetails principal) {

        Long groupId = principal.getGroupId();

        return ResponseEntity.ok(professorService.findAll(groupId));
    }


}
