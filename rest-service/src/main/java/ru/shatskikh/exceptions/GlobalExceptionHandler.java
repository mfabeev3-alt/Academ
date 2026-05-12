package ru.shatskikh.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shatskikh.entity.exceptions.EntityNotCreatedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.entity.exceptions.ScheduleConflictException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<Map<String, Object>> handleScheduleConflict(
            ScheduleConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", ex.getMessage()
                ));

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(

                        "timestamp", LocalDateTime.now(),
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
                        "message", ex.getMessage()
                ));

    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredToken(ExpiredJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", "Срок действия токена истёк, пожалуйста, авторизуйтесь заново."
                ));

    }

    @ExceptionHandler({SignatureException.class, io.jsonwebtoken.JwtException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidToken(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
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
                        .body("Вы не можете удалить предмет, пока с ним есть поля в расписании.");

            }

            if (errorMessage != null && errorMessage.contains("fk_schedule_item_professor_id")) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Вы не можете удалить преподавателя, пока с ним есть поля в расписании.");

            }

        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Ошибка целостности данных: нарушено ограничение уникальности.");

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadableException(HttpMessageNotReadableException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());

        String errorMessage = "Ошибка в формате JSON";
        if (ex.getCause() instanceof InvalidFormatException invalidEx) {
            if (invalidEx.getTargetType().isEnum()) {

                errorMessage = String.format("Недопустимое значение '%s'. Допустимые значениея: %s",
                        invalidEx.getValue(), Arrays.toString(invalidEx.getTargetType().getEnumConstants()));

            }

        } else {
            errorMessage = ex.getLocalizedMessage();
        }

        body.put("message", errorMessage);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }


}
