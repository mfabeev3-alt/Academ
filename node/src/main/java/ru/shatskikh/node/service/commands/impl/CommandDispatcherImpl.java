package ru.shatskikh.node.service.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.service.commands.CommandDispatcher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommandDispatcherImpl implements CommandDispatcher {

    private final Map<String, BotCommand> commands;

    @Autowired
    public CommandDispatcherImpl(List<BotCommand> allCommands) {
        this.commands = allCommands.stream()
                .collect(Collectors.toMap(BotCommand::getCommandIdentifier,cmd -> cmd));
    }

    @Override
    public void executeCommand(Update update, AppUser user) {

        String text = update.getMessage().getText();
        String commandName = text.split(" ")[0];

        BotCommand command = commands.get(commandName);

        if (command != null) {

            if(user.getUserRole().equals(command.getRequiredRole()) || user.getUserRole() == UserRole.ROLE_ADMIN) {
                command.execute(update, user);

            }
        }
    }
}
