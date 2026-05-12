package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.repository.ScheduleRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CurrentLessonCommand implements BotCommand {

    private final ScheduleRepository scheduleRepository;

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


    }
}
