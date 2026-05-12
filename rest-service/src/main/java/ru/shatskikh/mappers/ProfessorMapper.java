package ru.shatskikh.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shatskikh.DTO.ProfessorRequestDto;
import ru.shatskikh.DTO.ProfessorResponseDto;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.Schedule.Professor;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.repository.ProfessorRepository;
import ru.shatskikh.repository.GroupRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProfessorMapper {

    @Autowired
    protected ProfessorRepository professorRepository;

    @Autowired
    protected GroupRepository groupRepository;


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "groupId")
    public abstract Professor toEntity(ProfessorRequestDto professorRequestDto, Long groupId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "groupId")
    public abstract void entityFromDto(ProfessorRequestDto dto, Long groupId, @MappingTarget Professor professor);

    public abstract ProfessorResponseDto toResponse(Professor professor);

    protected Group mapGroup(Long id){

        return id == null ? null : groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Группа с ID " + id + " не найдена"));

    }


}
