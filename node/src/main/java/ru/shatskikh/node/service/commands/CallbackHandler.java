package ru.shatskikh.node.service.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;

public interface CallbackHandler {
    void handle(Update update, AppUser user);
    boolean isSupported(String data);
}
