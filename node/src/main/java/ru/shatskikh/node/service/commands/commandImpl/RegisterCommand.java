package ru.shatskikh.node.service.commands.commandImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

@Slf4j
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
        return "/registration";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_GUEST;
    }

    @Override
    public void execute(Update update, AppUser user) {

     var msg = update.getMessage();
     var chatId = msg.getChatId();

     if(user.getUserRole() == UserRole.ROLE_GUEST) {

     user.setUserState(UserState.AWAITING_REGISTRATION);
     user.setTempData("awaiting_fio");
     appUserRepository.save(user);

     messageSender.sendAnswer("Введите свои ФИО, как показано в примере: \n\nИванов Иван Иванович", chatId);

     } else {
         messageSender.sendAnswer("Вы уже зарегистрированы!", chatId);
     }

    }
}
