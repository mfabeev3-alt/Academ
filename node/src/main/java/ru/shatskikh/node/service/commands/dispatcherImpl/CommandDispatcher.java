package ru.shatskikh.node.service.commands.dispatcherImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.node.service.commands.BotCommand;

import ru.shatskikh.node.service.commands.Dispatcher;
import ru.shatskikh.node.utils.MessageSender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommandDispatcher implements Dispatcher {

    private final Map<String, BotCommand> commands;
    private final MessageSender messageSender;

    @Autowired
    public CommandDispatcher(List<BotCommand> allCommands, MessageSender messageSender) {
        this.commands = allCommands.stream()
                .collect(Collectors.toMap(BotCommand::getCommandIdentifier,cmd -> cmd));
        this.messageSender = messageSender;
    }

    @Override
    public void dispatch(Update update, AppUser user) {

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String commandName = text.split(":")[0];

        BotCommand command = commands.get(commandName);

        if (command != null) {

            if(user.getUserRole().hasAccess(command.getRequiredRole())) {
                command.execute(update, user);
            } else {
                messageSender.sendAnswer("У вас недостаточно прав для этой команды!", chatId);
            }
        } else {
            messageSender.sendAnswer("Неизвестная команда! Чтобы увидеть список команд, введите /help", chatId);
        }
    }
}
