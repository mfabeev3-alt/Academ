package ru.shatskikh.node.service.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.ProducerService;

public interface BotCommand {
    String getCommandIdentifier();
    UserRole getRequiredRole();
    void execute(Update update, AppUser user);
}
