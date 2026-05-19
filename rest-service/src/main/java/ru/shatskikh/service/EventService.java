package ru.shatskikh.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.DTO.EventRequestDto;
import ru.shatskikh.DTO.EventResponseDto;
import ru.shatskikh.entity.Event;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.mappers.EventMapper;
import ru.shatskikh.repository.EventRepository;
import ru.shatskikh.repository.GroupRepository;
import ru.shatskikh.utils.PermissionInspector;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventService {


    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final EventMapper eventMapper;
    private final PermissionInspector permissionInspector;

    public List<EventResponseDto> findAll(Long groupId) {

        List<Event> events = eventRepository.findAllByGroupId(groupId);
        List<EventResponseDto> dtos = new ArrayList<>();

        for (Event event : events) {
            dtos.add(eventMapper.toResponse(event));
        }

        return dtos;
    }

    @Transactional
    public EventResponseDto save(EventRequestDto dto, Long groupId)
            throws EntityNotFoundException, DataIntegrityViolationException {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Группа не найдена"));

        Event event = eventMapper.toEntity(dto, groupId);

        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponseDto edit(EventRequestDto dto, Long eventId, Long groupId)
            throws EntityNotFoundException, AccessDeniedException {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено."));

        if (!permissionInspector.isPermitted(groupId, event.getGroup().getId())) {
            throw new AccessDeniedException("Вы не можете редактировать чужое событие!");
        }

        event.setName(dto.name());
        event.setDescription(dto.description());

        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long eventId, Long groupId)
            throws DataIntegrityViolationException, AccessDeniedException {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено."));

        if (!permissionInspector.isPermitted(groupId, event.getGroup().getId())) {
            throw new AccessDeniedException("Вы не можете удалить чужое событие!");
        }

        eventRepository.deleteById(eventId);
    }

}
