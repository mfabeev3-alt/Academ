package ru.shatskikh.node.service.commands.commandImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.service.commands.service.RootService;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

@Component
public class RootCommand implements BotCommand {

    private final RootService rootService;
    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    @Autowired
    public RootCommand(RootService rootService, AppUserRepository appUserRepository, MessageSender messageSender) {
        this.rootService = rootService;
        this.appUserRepository = appUserRepository;
        this.messageSender = messageSender;
    }

    @Override
    public String getCommandIdentifier() {
        return "/iamroot";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_GUEST;
    }

    @Override
    public void execute(Update update, AppUser user) {

        String[] args = update.getMessage().getText().split(":");
        if(args.length < 2) return;

        if(rootService.verifyToken(args[1])){
            user.setUserRole(UserRole.ROLE_ADMIN);
            appUserRepository.save(user);
            messageSender.sendAnswer("Вы были наделены правами администратора!", update.getMessage().getChatId());
        } else {
            messageSender.sendAnswer("Неверный токен!", update.getMessage().getChatId());
        }

    }
}
