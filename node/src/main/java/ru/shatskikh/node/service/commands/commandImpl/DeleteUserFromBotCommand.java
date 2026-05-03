package ru.shatskikh.node.service.commands.commandImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.repository.AppUserRepository;

@Slf4j
@Component
public class DeleteUserFromBotCommand implements BotCommand {

    private final AppUserRepository appUserRepository;

    @Autowired
    public DeleteUserFromBotCommand(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

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

        appUserRepository.delete(user);

        log.info("User " + user.getTelegramUserId() + " was deleted from bot!");
    }
}
