package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository;

@Component
@RequiredArgsConstructor
public class CreateFaculty implements BotCommand {

    private final MessageSender messageSender;
    private final AppUserRepository appUserRepository;

    @Override
    public String getCommandIdentifier() {
        return "/create_faculty";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_ADMIN;
    }

    @Override
    public void execute(Update update, AppUser user) {

        var chatId = update.getMessage().getChatId();

        user.setUserState(UserState.CREATING_FACULTY);
        appUserRepository.save(user);

        messageSender.sendAnswer("Пришлите название факультета.", chatId);

    }
}
