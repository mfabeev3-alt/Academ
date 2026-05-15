package ru.shatskikh.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.SubjectRequestDto;
import ru.shatskikh.DTO.SubjectResponseDto;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.security.CustomUserDetails;
import ru.shatskikh.security.JwtService;
import ru.shatskikh.service.SubjectService;
import ru.shatskikh.utils.ErrorMessageBuilder;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SubjectController {

    private final ErrorMessageBuilder errorMessageBuilder;
    private final SubjectService subjectService;
    private final JwtService jwtService;

    @PostMapping("/add")
    @Operation(summary = "Добавление нового предмета")
    public ResponseEntity<SubjectResponseDto> add(@RequestBody @Valid SubjectRequestDto subjectRequestDto,
                                                  @AuthenticationPrincipal CustomUserDetails principal)
                                                    throws EntityNotCreatedException, EntityNotFoundException {

        Long groupId = principal.getGroupId();

        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.save(subjectRequestDto, groupId));

    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменение информации о предмете")
    public ResponseEntity<SubjectResponseDto> edit(@RequestBody @Valid SubjectRequestDto subjectRequestDto,
                                                   @PathVariable Long id,
                                                   @AuthenticationPrincipal CustomUserDetails principal)
            throws EntityNotCreatedException, EntityNotFoundException, AccessDeniedException {

        Long groupId = principal.getGroupId();

        return ResponseEntity.ok(subjectService.edit(subjectRequestDto, id, groupId));

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление предмета")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails principal)
                                        throws  AccessDeniedException, EntityNotFoundException {

        Long groupId = principal.getGroupId();

        subjectService.delete(id, groupId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Operation(summary = "Получение списка предметов")
    public ResponseEntity<List<SubjectResponseDto>> getAllSubject(@AuthenticationPrincipal CustomUserDetails principal) {

        Long groupId = principal.getGroupId();

        return ResponseEntity.ok(subjectService.findAll(groupId));
    }


}


