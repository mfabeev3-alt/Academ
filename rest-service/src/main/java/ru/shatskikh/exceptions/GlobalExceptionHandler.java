package ru.shatskikh.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex) {


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(

                        "timestamp", LocalDateTime.now(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", ex.getMessage()
                ));

    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errors = new HashMap<>();


        ex.getBindingResult().getAllErrors().forEach((error) -> {

            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

        });

        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", 400);
        errors.put("error", "Invalid data");

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityNotCreatedException.class)
    private ResponseEntity<Map<String, Object>> handleEntityNotCreatedException(
            EntityNotCreatedException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(

                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "error", "Not created",
                        "message", ex.getMessage()
                ));

    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
            EntityNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(

                        "timestamp", LocalDateTime.now(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()

                ));

    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredToken(ExpiredJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 401,
                        "error", "Unauthorized",
                        "message", "Срок действия токена истёк, пожалуйста, авторизуйтесь заново."

                ));

    }

    @ExceptionHandler({SignatureException.class, io.jsonwebtoken.JwtException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidToken(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", "Недействительный токен доступа."
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleIntegrityViolation(DataIntegrityViolationException ex) {


        Throwable rootCause = ex.getRootCause();

        if (rootCause != null) {
            String errorMessage = rootCause.getMessage();

            if (errorMessage != null && errorMessage.contains("uq_subject_name_group_id")) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Предмет с таким названием уже существует в вашей группе.");
            }

            if (errorMessage != null && errorMessage.contains("uq_professor_name_group_id")) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Преподаватель с таким именем уже существует в вашей группе.");

            }

            if (errorMessage != null && errorMessage.contains("fk_schedule_item_subject_id")) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Вы не можете удалить предмет, пока с ним есть поля в расписании");

            }

            if (errorMessage != null && errorMessage.contains("fk_schedule_item_professor_id")) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Вы не можете удалить преподавателя, пока с ним есть поля в расписании");

            }


        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Ошибка целостности данных: нарушено ограничение уникальности.");

    }

}
