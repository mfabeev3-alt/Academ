package ru.shatskikh.mappers;


import org.mapstruct.Mapper;
import ru.shatskikh.DTO.SubjectRequestDto;
import ru.shatskikh.DTO.SubjectResponseDto;
import ru.shatskikh.entity.Schedule.Subject;

@Mapper (componentModel = "spring")
public interface SubjectMapper {
    Subject convertToSubject(SubjectRequestDto subjectRequestDto);
    SubjectResponseDto convertToSubjectResponseDto(Subject subject);

}
