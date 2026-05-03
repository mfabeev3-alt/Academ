package ru.shatskikh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.DTO.SubjectRequestDto;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.Schedule.Subject;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.repositories.SubjectRepository;
import ru.shatskikh.repository.GroupRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;


    @Transactional
    public void save(SubjectRequestDto dto, Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Группа не найдена"));

        Subject subject = Subject.builder().name(dto.name()).group(group).build();

        subjectRepository.save(subject);



    }

}
