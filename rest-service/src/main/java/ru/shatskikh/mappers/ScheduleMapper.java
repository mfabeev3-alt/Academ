package ru.shatskikh.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shatskikh.DTO.ScheduleRequestDto;
import ru.shatskikh.DTO.ScheduleResponseDto;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.Schedule.Professor;
import ru.shatskikh.entity.Schedule.Schedule;
import ru.shatskikh.entity.Schedule.Subject;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.repository.ProfessorRepository;
import ru.shatskikh.repository.SubjectRepository;
import ru.shatskikh.repository.GroupRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public abstract class ScheduleMapper {

   @Autowired
   protected SubjectRepository subjectRepository;

   @Autowired
   protected ProfessorRepository professorRepository;

   @Autowired
   protected GroupRepository groupRepository;

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "subject", source = "dto.subjectId")
   @Mapping(target = "professor", source = "dto.professorId")
   @Mapping(target = "group", source = "groupId")
   @Mapping(target = "activeWeeks", source = "dto.activeWeeks")

   public abstract Schedule toEntity(ScheduleRequestDto dto, Long groupId);

   @Mapping(target = "subjectName", source = "subject.name")
   @Mapping(target = "professorName", source = "professor.name")
   @Mapping(target = "professorContact", source = "professor.contact")
   public abstract ScheduleResponseDto toResponse(Schedule schedule);

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "subject", source = "dto.subjectId")
   @Mapping(target = "professor", source = "dto.professorId")
   @Mapping(target = "group", source = "groupId")
   @Mapping(target = "activeWeeks", source = "dto.activeWeeks")
   public abstract void updateEntityFromDto(ScheduleRequestDto dto, Long groupId, @MappingTarget Schedule item);


   protected Subject mapSubject(Long id){
      return id == null ? null : subjectRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException("Предмет с ID " + id + " не найден."));
   }

   protected Professor mapProfessor(Long id){
      return id == null ? null : professorRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException("Преподаватель с ID " + id + " не найден."));
   }

   protected Group mapGroup(Long id){
      return id == null ? null : groupRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException("Группа с ID " + id + " не найдена."));
   }


}


