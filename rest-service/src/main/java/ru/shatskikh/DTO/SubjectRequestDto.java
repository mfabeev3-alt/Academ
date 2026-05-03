package ru.shatskikh.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequestDto(

        @NotBlank(message = "Название предмета не может быть пустым")
        @Size( max = 255, message = "Название предмета не может превышать 255 символов")
        String name

){}
