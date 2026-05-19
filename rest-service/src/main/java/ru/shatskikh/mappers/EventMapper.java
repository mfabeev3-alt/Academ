package ru.shatskikh.mappers;



import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shatskikh.DTO.EventRequestDto;
import ru.shatskikh.DTO.EventResponseDto;
import ru.shatskikh.entity.Event;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.repository.EventRepository;
import ru.shatskikh.repository.GroupRepository;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EventMapper {

    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", source = "groupId")
    public abstract Event toEntity(EventRequestDto eventRequestDto , Long groupId);

    public abstract EventResponseDto toResponse(Event event);

    protected Group mapGroup(Long id){

        return id == null ? null : groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Группа с ID " + id + " не найдена"));

    }


}

