package ru.shatskikh.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shatskikh.DTO.SubjectRequestDto;
import ru.shatskikh.DTO.SubjectResponseDto;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.Schedule.Subject;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.repository.GroupRepository;

@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SubjectMapper {

    @Autowired
    protected GroupRepository groupRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "groupId")
    public abstract Subject toEntity(SubjectRequestDto subjectRequestDto, Long groupId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "groupId")
    public abstract void entityFromDto(SubjectRequestDto dto, Long groupId, @MappingTarget Subject subject);

    public abstract SubjectResponseDto toResponse(Subject subject);

    protected Group mapGroup(Long id){

        return id == null ? null : groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Группа с ID " + id + " не найдена"));

    }

}
