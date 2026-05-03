package ru.shatskikh.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.shatskikh.DTO.SubjectRequestDto;
import ru.shatskikh.exceptions.ScheduleNotCreatedException;
import ru.shatskikh.security.JwtService;
import ru.shatskikh.service.SubjectService;
import ru.shatskikh.utils.ErrorMessageBuilder;

@Controller
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final ErrorMessageBuilder errorMessageBuilder;
    private final SubjectService subjectService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(@RequestBody @Valid SubjectRequestDto subjectRequestDto,
                                          @RequestHeader("Authorization") String authHeader,
                                          BindingResult bindingResult) throws ScheduleNotCreatedException {

        if(bindingResult.hasErrors()) {

            throw new ScheduleNotCreatedException(errorMessageBuilder.buildErrorMessage(bindingResult));

        }

        String token = authHeader.substring(7);
        Long groupId = jwtService.getGroupIdFromToken(token);

        subjectService.save(subjectRequestDto, groupId);


        return null;
    }


}


