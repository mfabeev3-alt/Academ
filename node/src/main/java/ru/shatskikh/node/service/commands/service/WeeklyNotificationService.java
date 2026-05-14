package ru.shatskikh.node.service.commands.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Schedule.Schedule;
import ru.shatskikh.entity.enums.DayOfWeek;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class WeeklyNotificationService {

    private final ScheduleRepository scheduleRepository;
    private final WeekService weekService;
    private final AppUserRepository userRepository;
    private final MessageSender messageSender;

    @Scheduled(cron = "0 0 18 * * SUN")
    public void sendWeeklyScheduleNotification() {
        log.info("Запуск автоматической рассылки расписания на следующую неделю");

        LocalDate nextMonday = LocalDate.now().plusDays(1);
        int nextWeekNum = weekService.getWeekForDate(nextMonday);

        List<AppUser> users = userRepository.findByUserRoleIn(List.of(UserRole.ROLE_STUDENT, UserRole.ROLE_LEADER));

        for (AppUser user : users) {
            if (user.getGroup() == null) continue;

            String scheduleText = formatWeeklySchedule(user.getGroup().getId(), nextWeekNum);

            messageSender.sendAnswer(scheduleText, user.getTelegramUserId());
        }
    }

    private String formatWeeklySchedule(Long groupId, int weekNum) {
        List<Schedule> lessons = scheduleRepository.findAllByGroupIdAndWeekNum(groupId, weekNum);

        if (lessons.isEmpty()) {
            return "🎉 На следующей неделе (" + weekNum + "-я в цикле) пар нет! Отдыхайте.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🗓 **Расписание на следующую неделю (").append(weekNum).append("-я неделя):**\n");

        DayOfWeek lastDay = null;
        for (Schedule item : lessons) {
            if (item.getDayOfWeek() != lastDay) {
                sb.append("\n🔹 **").append(item.getDayOfWeek().getDescription().toUpperCase()).append("**\n");
                lastDay = item.getDayOfWeek();
            }
            sb.append("  ").append(item.getStartTime()).append(" - ").append(item.getEndTime())
                    .append(" | ").append(item.getSubject().getName()).append("\n");
        }
        return sb.toString();
    }

}
