package ru.shatskikh.node.service.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserState;

public interface StateHandler {
    void handle(Update update, AppUser user);
    UserState getSupportedState();
}
