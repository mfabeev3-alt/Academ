package ru.shatskikh.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.ProfessorRequestDto;
import ru.shatskikh.DTO.ProfessorResponseDto;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.mappers.ProfessorMapper;
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
    public ResponseEntity<ProfessorResponseDto> add(@RequestBody @Valid ProfessorRequestDto professorRequestDto,
                                                    @RequestHeader("Authorization") String authHeader)
            throws EntityNotCreatedException, EntityNotFoundException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(professorService.save(professorRequestDto, groupId));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfessorResponseDto> edit(@RequestBody @Valid ProfessorRequestDto professorRequestDto,
                                                   @PathVariable Long id,
                                                   @RequestHeader("Authorization") String authHeader)
            throws EntityNotCreatedException, EntityNotFoundException, AccessDeniedException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.ok(professorService.edit(professorRequestDto, id, groupId));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader)
            throws EntityNotFoundException, AccessDeniedException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        professorService.delete(id, groupId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<ProfessorResponseDto>> index(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.ok(professorService.findAll(groupId));
    }


}
