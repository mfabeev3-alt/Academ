package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Schedule.Schedule;
import ru.shatskikh.entity.enums.DayOfWeek;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.service.commands.service.WeekService;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentLessonCommand implements BotCommand {

    private final ScheduleRepository scheduleRepository;
    private final MessageSender messageSender;
    private final WeekService weekService;

    @Override
    public String getCommandIdentifier() {
        return "/current_lesson";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_STUDENT;
    }

    @Override
    public void execute(Update update, AppUser user) {

        LocalTime now = LocalTime.now();
        DayOfWeek day = DayOfWeek.of(LocalDate.now().getDayOfWeek());
        Integer currentWeek = weekService.getCurrentWeek();

        Long groupId = user.getGroup().getId();
        Long chatId = update.getMessage().getChatId();

        log.info("Неделя: " + currentWeek + "; день: " + day.name() + "; время: " + now + "; ID группы: " + groupId);

        List<Schedule> remainingItems = scheduleRepository.findCurrentAndRemainingItems(groupId, day, now, currentWeek);

        if (remainingItems.isEmpty()) {

            List<Schedule> nextDayLessons = Collections.emptyList();
            int dayOffset = 1;
            LocalDate targetDate = LocalDate.now();

            while (dayOffset <= 7) {

                targetDate = LocalDate.now().plusDays(dayOffset);
                DayOfWeek nextDay = DayOfWeek.of(targetDate.getDayOfWeek());

                int nextWeek = weekService.getWeekForDate(targetDate);

               nextDayLessons = scheduleRepository.findFirstLessonByDayAndWeek(
                        groupId, nextDay, nextWeek
                );
                if (!nextDayLessons.isEmpty()) {
                    break;
                }
                dayOffset++;
            }

            if (!nextDayLessons.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                String dayName = (dayOffset == 1) ? "завтра" : nextDayLessons.getFirst().getDayOfWeek().getDescription().toLowerCase();

                sb.append("\uD83D\uDCCC На сегодня пар больше нет.\n");
                sb.append("\uD83D\uDDD3 Расписание на ").append(dayName).append(" (").append(targetDate).append("):\n\n");

                for (Schedule lesson : nextDayLessons) {
                    sb.append("\uD83D\uDD52 Время: ").append(lesson.getStartTime()).append(" - ").append(lesson.getEndTime()).append("\n")
                            .append("\uD83D\uDCD6 Предмет: ").append(lesson.getSubject().getName()).append("\n")
                            .append("\uD83D\uDCCD Кабинет: ").append(lesson.getRoom()).append("\n")
                            .append("\uD83D\uDC64 Преподаватель: ").append(lesson.getProfessor().getName()).append(", ")
                            .append(lesson.getProfessor().getContact()).append("\n")
                            .append("----------------------------\n");
                }

                messageSender.sendAnswer(sb.toString(), chatId);
            } else {
                messageSender.sendAnswer("Пар не найдено на ближайшую неделю. Отдыхаем! 🎉", chatId);
            }

        } else {

            Schedule firstFound = remainingItems.getFirst();

            if (!now.isAfter(firstFound.getStartTime()) && now.isBefore(firstFound.getEndTime())) {

                String output = "❗ Сейчас идёт пара: " + firstFound.getSubject().getName() +
                        "\n\uD83D\uDD52 Закончится в " + firstFound.getEndTime() +
                        "\n\uD83D\uDCCD Кабинет: " + firstFound.getRoom() +
                        "\n\uD83D\uDCD6 Преподаватель: " + firstFound.getProfessor().getName() + ", " + firstFound.getProfessor().getContact();

                messageSender.sendAnswer(output, chatId);

            } else {

                String output = "\uD83C\uDF38 Сейчас пар нет." +
                        "\n✅ Ближайшая пара: " + firstFound.getSubject().getName() +
                        "\n\uD83D\uDD52 Начнется в " + firstFound.getStartTime() +
                        "\n\uD83D\uDCCD Кабинет: " + firstFound.getRoom() +
                        "\n\uD83D\uDCD6 Преподаватель: " + firstFound.getProfessor().getName() + ", " + firstFound.getProfessor().getContact();

                messageSender.sendAnswer(output, chatId);
            }

        }
    }
}
