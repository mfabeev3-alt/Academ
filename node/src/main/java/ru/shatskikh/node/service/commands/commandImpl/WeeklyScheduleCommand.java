package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Component
@RequiredArgsConstructor
public class WeeklyScheduleCommand implements BotCommand {

    private final WeekService weekService;
    private final ScheduleRepository scheduleRepository;
    private final MessageSender messageSender;

    @Override
    public String getCommandIdentifier() {
        return "/weekly_schedule";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_STUDENT;
    }

    @Override
    public void execute(Update update, AppUser user) {

        Long groupId = user.getGroup().getId();
        Long chatId = update.getMessage().getChatId();

        if(user.getGroup() == null){

            messageSender.sendAnswer(
                    "❌ Вы не можете посмотреть расписание, поскольку вы не приписаны ни к одной группе!", chatId);

            return;
        }

        int currentWeek = weekService.getCurrentWeek();
        List<Schedule> schedule = scheduleRepository.findAllByGroupIdAndWeekNum(groupId, currentWeek);

        if (schedule.isEmpty()) {
            messageSender.sendAnswer("На этой неделе пар нет. Отдыхаем!", chatId);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Расписание на ").append(currentWeek).append("-ю неделю:\n\n");

        DayOfWeek lastDay = null;

        for (Schedule item : schedule) {
            // Добавляем заголовок дня, если он изменился
            if (item.getDayOfWeek() != lastDay) {
                sb.append("\n\uD83D\uDD39 **").append(item.getDayOfWeek().getDescription().toUpperCase()).append("**\n");
                lastDay = item.getDayOfWeek();
            }

            sb.append("  ").append(item.getStartTime()).append(" - ").append(item.getEndTime())
                    .append(" | ").append(item.getSubject().getName())
                    .append(" (").append(item.getRoom()).append(") \n ")
                    .append(item.getProfessor().getName()).append(", ")
                    .append(item.getProfessor().getContact()).append(" \n");
        }

        messageSender.sendAnswer(sb.toString(), chatId);

    }
}
