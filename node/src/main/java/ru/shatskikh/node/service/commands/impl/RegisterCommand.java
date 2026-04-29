package ru.shatskikh.node.service.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.ProducerService;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

@Component
public class RegisterCommand implements BotCommand {

    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    @Autowired
    public RegisterCommand(AppUserRepository appUserRepository, MessageSender messageSender) {
        this.appUserRepository = appUserRepository;
        this.messageSender = messageSender;
    }

    @Override
    public String getCommandIdentifier() {
        return "/register";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_GUEST;
    }

    @Override
    public void execute(Update update, AppUser user) {

     var msg = update.getMessage();
     var chatId = msg.getChatId();
     var output = "";
    user.setUserState(UserState.AWAITING_FIO);
    appUserRepository.save(user);

    messageSender.sendAnswer(output, chatId);

    }
}
