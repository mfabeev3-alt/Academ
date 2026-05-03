package ru.shatskikh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.DTO.SubjectRequestDto;
import ru.shatskikh.DTO.SubjectResponseDto;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.Schedule.Subject;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.mappers.SubjectMapper;
import ru.shatskikh.repositories.SubjectRepository;
import ru.shatskikh.repository.GroupRepository;
import ru.shatskikh.utils.PermissionInspector;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final SubjectMapper subjectMapper;
    private final PermissionInspector permissionInspector;


    public List<SubjectResponseDto> findAll(Long groupId){

        List<Subject> subjects = subjectRepository.findAllByGroupId(groupId);
        List<SubjectResponseDto> dtos = new ArrayList<>();

        for(Subject subject: subjects){

            dtos.add(subjectMapper.convertToSubjectResponseDto(subject));

        }

        return dtos;

    }

    @Transactional
    public SubjectResponseDto save(SubjectRequestDto dto, Long groupId) throws EntityNotFoundException, DataIntegrityViolationException {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Группа не найдена"));

        Subject subject = Subject.builder().name(dto.name()).group(group).build();

        return subjectMapper.convertToSubjectResponseDto(subjectRepository.save(subject));

    }

    @Transactional
    public SubjectResponseDto edit(SubjectRequestDto dto, Long subjectId, Long groupId) throws EntityNotFoundException, AccessDeniedException {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет не найден."));

        if (!permissionInspector.isPermitted(groupId, subject.getGroup().getId())) {
            throw new AccessDeniedException("Вы не можете редактировать чужой предмет!");
        }

        subject.setName(dto.name());

        return subjectMapper.convertToSubjectResponseDto(subjectRepository.save(subject));

    }

    @Transactional
    public void delete(Long subjectId, Long groupId) throws DataIntegrityViolationException, AccessDeniedException {


        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет не найден."));

        if (permissionInspector.isPermitted(groupId, subject.getGroup().getId()))

        subjectRepository.deleteById(subjectId);

    }


}
