package ru.shatskikh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.DTO.ProfessorRequestDto;
import ru.shatskikh.DTO.ProfessorResponseDto;
import ru.shatskikh.entity.Schedule.Professor;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.mappers.ProfessorMapper;
import ru.shatskikh.repository.ProfessorRepository;
import ru.shatskikh.repository.GroupRepository;
import ru.shatskikh.utils.PermissionInspector;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ProfessorMapper professorMapper;
    private final GroupRepository groupRepository;
    private final PermissionInspector permissionInspector;


    public List<ProfessorResponseDto> findAll(Long groupId){

        List<Professor> professors = professorRepository.findAllByGroupId(groupId);
        List<ProfessorResponseDto> dtos = new ArrayList<>();

        for(Professor professor: professors){

            dtos.add(professorMapper.toResponse(professor));

        }

        return dtos;
    }

    @Transactional
    public ProfessorResponseDto save(ProfessorRequestDto dto, Long groupId)
            throws EntityNotFoundException, DataIntegrityViolationException {

        Professor professor = professorMapper.toEntity(dto, groupId);

        return professorMapper.toResponse(professorRepository.save(professor));

    }

    @Transactional
    public ProfessorResponseDto edit(ProfessorRequestDto dto, Long professorId, Long groupId)
            throws EntityNotFoundException, AccessDeniedException {

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Преподаватель не найден."));


        if (!permissionInspector.isPermitted(groupId, professor.getGroup().getId())) {
            throw new AccessDeniedException("Вы не можете редактировать чужого преподавателя!");
        }

       professorMapper.entityFromDto(dto, groupId, professor);

        return professorMapper.toResponse(professorRepository.save(professor));

    }

    @Transactional
    public void delete(Long professorId, Long groupId)
            throws DataIntegrityViolationException, AccessDeniedException {

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Преподаватель не найден."));

        if (permissionInspector.isPermitted(groupId, professor.getGroup().getId()))

            professorRepository.deleteById(professorId);

    }



}
