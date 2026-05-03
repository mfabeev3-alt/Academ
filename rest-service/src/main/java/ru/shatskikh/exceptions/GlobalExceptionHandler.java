package ru.shatskikh.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfessorNotCreatedException.class)
    private ResponseEntity<ErrorResponse> handleProfessorNotCreatedException(
            ProfessorNotCreatedException ex) {

        ErrorResponse response = new ErrorResponse(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(SubjectNotCreatedException.class)
    private ResponseEntity<ErrorResponse> handleSubjectNotCreatedException(
            SubjectNotCreatedException ex) {

        ErrorResponse response = new ErrorResponse(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ScheduleNotCreatedException.class)
    private ResponseEntity<ErrorResponse> handleScheduleNotCreatedException(
            ScheduleNotCreatedException ex) {

        ErrorResponse response = new ErrorResponse(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex) {

        ErrorResponse response = new ErrorResponse(ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredToken(ExpiredJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "Status", 401,
                        "error", "Unauthorized",
                        "message", "Cрок действия токена истёк, пожалуйста, авторизуйтесь заново."

                ));

    }


    @ExceptionHandler({SignatureException.class, io.jsonwebtoken.JwtException.class})
    public ResponseEntity<Map<String, Object>> handleInvalidToken(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "Status", 403,
                        "error", "Forbidden",
                        "message", "Недействительный токен доступа"
                ));
    }
}
