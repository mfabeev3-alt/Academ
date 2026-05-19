package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetSemesterStartDateCommand implements BotCommand {

    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    @Override
    public String getCommandIdentifier() {
        return "/set_semester_start_date";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_MODERATOR;
    }

    @Override
    public void execute(Update update, AppUser user) {

        Long chatId = update.getMessage().getChatId();

        user.setUserState(UserState.AWAITING_SEMESTER_START_DATE);
        appUserRepository.save(user);

        String output = """
                📅 Введите дату начала семестра в формате:
                ДД.ММ.ГГГГ
                
                Пример:
                01.09.2026
                """;

        messageSender.sendAnswer(output, chatId);
    }
}