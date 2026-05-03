package ru.shatskikh.node.service.commands.commandImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

@Slf4j
@Component
public class StartCommand implements BotCommand {

    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    private String START_MESSAGE = "Hello! This is Academ bot! \n\n " +
            "To get access to bot's functions type /registration";


    @Autowired
    public StartCommand(AppUserRepository appUserRepository, MessageSender messageSender) {
        this.appUserRepository = appUserRepository;
        this.messageSender = messageSender;
    }

    @Override
    public String getCommandIdentifier() {
        return "/start";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_GUEST;
    }

    @Override
    public void execute(Update update, AppUser user) {

        var msg = update.getMessage();
        var chatId = msg.getChatId();


        if (user.getFio() != null) {
            messageSender.sendAnswer("Welcome back " + user.getFio() + "!", chatId);
        } else {
            messageSender.sendAnswer(START_MESSAGE, chatId);

        }

    }


}
