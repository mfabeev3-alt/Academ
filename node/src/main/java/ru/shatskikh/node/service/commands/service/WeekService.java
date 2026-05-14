package ru.shatskikh.node.service.commands.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.shatskikh.entity.Semester;
import ru.shatskikh.repository.SemesterRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeekService {

    private final SemesterRepository semesterRepository;

    public Integer getCurrentWeek() throws IllegalStateException{
        LocalDate startDate = getSemesterStartDate();
        LocalDate now = LocalDate.now();

        if(now.isBefore(startDate)) {
            return 0;
        }

        long weekSinceStart = ChronoUnit.WEEKS.between(startDate, now);

        int result = (int)(weekSinceStart % 4) + 1;

        log.info("Текущая неделя: " + result + ". Дата отчёта: " + startDate);

        return result;

    }

    public Integer getWeekForDate(LocalDate targetDate){

        LocalDate startDate = getSemesterStartDate();

        long weekSinceStart = ChronoUnit.WEEKS.between(startDate, targetDate);

        int result = (int)(weekSinceStart % 4) + 1;

        log.info("Текущая неделя: " + result + ". Дата отчёта: " + startDate);

        return result;

    }
    private LocalDate getSemesterStartDate() {
        return semesterRepository.findByIsActiveTrue()
                .map(Semester::getStartDate)
                .orElseThrow(() -> new IllegalStateException("Активный семестр не найден в бд!"));
    }

}
