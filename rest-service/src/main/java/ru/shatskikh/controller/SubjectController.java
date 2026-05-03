package ru.shatskikh.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.shatskikh.DTO.SubjectRequestDto;
import ru.shatskikh.DTO.SubjectResponseDto;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.security.JwtService;
import ru.shatskikh.service.SubjectService;
import ru.shatskikh.utils.ErrorMessageBuilder;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final ErrorMessageBuilder errorMessageBuilder;
    private final SubjectService subjectService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<SubjectResponseDto> add(@RequestBody @Valid SubjectRequestDto subjectRequestDto,
                                                  @RequestHeader("Authorization") String authHeader)
                                                    throws EntityNotCreatedException, EntityNotFoundException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.save(subjectRequestDto, groupId));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> edit(@RequestBody @Valid SubjectRequestDto subjectRequestDto,
                                                   @PathVariable Long id,
                                                   @RequestHeader("Authorization") String authHeader)
            throws EntityNotCreatedException, EntityNotFoundException, AccessDeniedException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.ok(subjectService.edit(subjectRequestDto, id, groupId));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader)
                                        throws  AccessDeniedException, EntityNotFoundException {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        subjectService.delete(id, groupId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping()
    public ResponseEntity<List<SubjectResponseDto>> index(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        return ResponseEntity.ok(subjectService.findAll(groupId));
    }




}


