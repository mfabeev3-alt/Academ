package ru.shatskikh.node.service.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;

public interface Dispatcher {

    void dispatch(Update update, AppUser user);

}
