package ru.shatskikh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.DTO.ScheduleRequestDto;
import ru.shatskikh.DTO.ScheduleResponseDto;

import ru.shatskikh.entity.Schedule.Schedule;

import ru.shatskikh.entity.enums.DayOfWeek;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.entity.exceptions.ScheduleConflictException;
import ru.shatskikh.mappers.ScheduleMapper;

import ru.shatskikh.repository.ScheduleRepository;
import ru.shatskikh.utils.PermissionInspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final PermissionInspector permissionInspector;


    public List<ScheduleResponseDto> findAll(Long groupId){

        List<Schedule> items = scheduleRepository.findAllByGroupId(groupId);
        List<ScheduleResponseDto> dtos = new ArrayList<>();

        for(Schedule item: items){

            dtos.add(scheduleMapper.toResponse(item));
        }

        return dtos;
    }

    public List<ScheduleResponseDto> findScheduleForDay(Long groupId, DayOfWeek dayOfWeek, Integer week)
            throws EntityNotFoundException{

        List<Schedule> items = scheduleRepository.findScheduleForDayAndWeek(groupId, dayOfWeek, week);

        return toDtos(items);
    }

    public List<ScheduleResponseDto> findScheduleForWeek(Long groupId, Integer week)
            throws EntityNotFoundException{

        List<Schedule> items = scheduleRepository.findScheduleForWeek(groupId, week);

        return toDtos(items);
    }

    @Transactional
    public ScheduleResponseDto save(ScheduleRequestDto dto, Long groupId)
            throws EntityNotFoundException, DataIntegrityViolationException, ScheduleConflictException{

        checkScheduleConflict(dto, groupId, null);

        Schedule schedule = scheduleMapper.toEntity(dto, groupId);

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return scheduleMapper.toResponse(savedSchedule);

    }

    @Transactional
    public ScheduleResponseDto edit(ScheduleRequestDto dto, Long scheduleItemId, Long groupId)
            throws EntityNotFoundException, DataIntegrityViolationException, AccessDeniedException, ScheduleConflictException{

        Schedule item = scheduleRepository.findById(scheduleItemId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена."));

        if (!permissionInspector.isPermitted(groupId, item.getGroup().getId())) {
            throw new AccessDeniedException("Вы не можете редактировать чужую запись!");
        }

        checkScheduleConflict(dto, groupId, scheduleItemId);

        scheduleMapper.updateEntityFromDto(dto, groupId, item);

        return scheduleMapper.toResponse(scheduleRepository.save(item));
    }

    @Transactional
    public void delete(Long scheduleItemId, Long groupId)
            throws EntityNotFoundException, DataIntegrityViolationException, AccessDeniedException {

      Schedule item = scheduleRepository.findById(scheduleItemId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена."));

        if (permissionInspector.isPermitted(groupId, item.getGroup().getId()))

            scheduleRepository.deleteById(scheduleItemId);

    }

    private List<ScheduleResponseDto> toDtos(List<Schedule> items) {

        List<ScheduleResponseDto> dtos = new ArrayList<>();

        for (Schedule schedule: items) {

            dtos.add(scheduleMapper.toResponse(schedule));

        }

        return dtos;
    }

    private void checkScheduleConflict(ScheduleRequestDto dto, Long groupId, Long excludeId) {

        List<Schedule> existingSchedule = scheduleRepository.findByGroupIdAndDayOfWeek(groupId, dto.dayOfWeek());

        boolean hasConflict = existingSchedule.stream()
                .filter(s -> !s.getId().equals(excludeId))
                .filter(s -> s.getStartTime().equals(dto.startTime()))
                .anyMatch(s -> !Collections.disjoint(s.getActiveWeeks(), dto.activeWeeks()));

        if (hasConflict) {
            throw new ScheduleConflictException(
                    String.format("Конфликт: в %s время %s на указанных неделях уже занято другой парой!",
                            dto.dayOfWeek(), dto.startTime())
            );
        }
    }
}