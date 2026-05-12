package ru.shatskikh.node.service.commands.stateHandlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.exceptions.UserNotFoundException;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;


@Component
@Slf4j
@RequiredArgsConstructor
public class SetRoleStateHandler implements StateHandler {

    private final MessageSender messageSender;
    private final AppUserRepository appUserRepository;

    @Override
    public UserState getSupportedState() {
        return UserState.AWAITING_USER_FOR_ROLE;
    }

    @Override
    public void handle(Update update, AppUser moderator) {
        var message = update.getMessage();
        var chatId = moderator.getTelegramUserId();

        Long targetUserId = null;
        //check what moderator sent

        if(message.hasText() && message.getForwardFrom() == null) {

            var username = message.getText();

            if(!username.startsWith("@")) {
                messageSender.sendAnswer("Введите username в формате @username", chatId);
                return;
            }

            username = username.substring(1);

            try {

            targetUserId = appUserRepository.findByUsername(username)
                    .map(AppUser::getTelegramUserId).orElseThrow(() ->
                            new UserNotFoundException("Пользователь не найден!"));

            } catch (UserNotFoundException ex) {

                messageSender.sendAnswer("Неверный @username!", chatId);
                log.debug(ex.getMessage());
                return;
            }

        } else if(message.hasContact()){

            targetUserId = message.getContact().getUserId();

        } else if (message.getForwardFrom() != null) {

            targetUserId = message.getForwardFrom().getId();

        } else {
            messageSender.sendAnswer("Неверный формат! Отправьте @username, контакт или перешлите сообщение", chatId);
            return;
        }

        String roleStr = moderator.getTempData();
        UserRole role = UserRole.valueOf(roleStr);

        appUserRepository.findAppUserByTelegramUserId(targetUserId).ifPresentOrElse(
                targetUser -> {
                    targetUser.setUserRole(role);
                   appUserRepository.save(targetUser);

                   moderator.setUserState(UserState.IDLE);
                   moderator.setTempData(null);
                   appUserRepository.save(moderator);
                   messageSender.sendAnswer(
                           String.format("Успешно! Пользователю @%s присвоена роль: %s", targetUser.getUsername(), role.toString()),
                           chatId);

                   messageSender.sendAnswer("Вам присвоина роль " + role.toString() + "!", targetUser.getTelegramUserId());

                },
                () -> messageSender.sendAnswer("Пользователь не найден в базе данных!", chatId)
        );

    }

}
