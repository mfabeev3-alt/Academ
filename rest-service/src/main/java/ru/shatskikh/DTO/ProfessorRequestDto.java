package ru.shatskikh.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfessorRequestDto(
        @NotBlank(message = "Имя преподавателя не может быть путым")

        @Size (max = 255, message = "Имя не должно превышать 255 символов")
        String name,

        @Size(max = 255, message = "Контактная информация не должна превышать 255 символов")
        String contact

) {}
