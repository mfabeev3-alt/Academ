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


@Component
@RequiredArgsConstructor
public class CancelCommand implements BotCommand {

    private final MessageSender messageSender;
    private final AppUserRepository appUserRepository;

    @Override
    public String getCommandIdentifier() {
        return "/cancel";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_GUEST;
    }

    @Override
    public void execute(Update update, AppUser user) {

        var message = update.getMessage();
        var chaId = message.getChatId();

        user.setUserState(UserState.IDLE);
        user.setTempData(null);

        appUserRepository.save(user);

        messageSender.sendAnswer("Действие успешно отменено!", chaId);

    }
}
