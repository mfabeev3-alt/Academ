package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpCommand implements BotCommand {

    private final MessageSender messageSender;

    @Override
    public String getCommandIdentifier() {
        return "/help";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_GUEST;
    }

    @Override
    public void execute(Update update, AppUser user) {
        List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commands = new ArrayList<>();

        var chatId = update.getMessage().getChatId();

        commands.add(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand("/cancel", "Отменить действие"));

        if (user.getUserRole().getLevel() > UserRole.ROLE_LEADER.getLevel()) {
            commands.add(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand("/create_new_group", "Создать группу"));
            commands.add(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand("/broadcast", "Массовая рассылка"));
            commands.add(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand("/delete_user_from_bot", "Удалить пользователя"));
            commands.add(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand("/delete_group", "Удалить группу"));
            commands.add(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand("/create_faculty", "Создать факультет"));
            commands.add(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand("/delete_faculty", "Создать факультет"));
        }

        messageSender.setPersonalizedCommands(chatId, commands);

    }
}
