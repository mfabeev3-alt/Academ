package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.repository.AppUserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteUserFromBotCommand implements BotCommand {

    private final AppUserRepository appUserRepository;


    @Override
    public String getCommandIdentifier() {
        return "/delete_user_from_bot";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_MODERATOR;
    }

    @Override
    public void execute(Update update, AppUser user) {

        user.setUserState(UserState.AWAITING_USER_FOR_DELETE);

        String output = "Пришлите @username, контакт и сообщение пользователя, которого вы хотите удалить";

    }
}
