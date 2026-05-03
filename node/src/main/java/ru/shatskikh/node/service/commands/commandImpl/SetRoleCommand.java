package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class SetRoleCommand implements BotCommand {

    private final MessageSender messageSender;
    private final AppUserRepository appUserRepository;

    @Override
    public String getCommandIdentifier() {
        return "/set_role";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_MODERATOR;
    }

    @Override
    public void execute(Update update, AppUser moderator) {

        var message = update.getMessage();
        var chatId = message.getChatId();
        var command = message.getText();

        String[] parts = command.split(":");

        if (parts.length != 2) {
            messageSender.sendAnswer("Использование: /set_role:<ROLE_NAME> (/set_role:ADMIN)", chatId);
            return;
        }

        try {

            UserRole role = UserRole.valueOf(parts[1].toUpperCase());

            moderator.setUserState(UserState.AWAITING_USER_FOR_ROLE);
            moderator.setTempData(role.name());
            appUserRepository.save(moderator);

            messageSender.sendAnswer(
                    "Отправьте @username, контакт или перешлите сообщение пользователя," +
                            " которого вы хотите наделить правами", chatId);

        } catch (IllegalArgumentException ex) {
            messageSender.sendAnswer("Неверная роль! Доступные роли: STUDENT, LEADER, MODERATOR", chatId);
            log.debug("Wrong role: " + parts[1].toUpperCase());
            log.debug(ex.getMessage());
        }
    }
}
