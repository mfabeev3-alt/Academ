package ru.shatskikh.node.service.commands.stateHandlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.exceptions.UserNotFoundException;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteUserFromBotStateHandler implements StateHandler {

    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    @Override
    @Transactional
    public void handle(Update update, AppUser moderator) {


        var message = update.getMessage();
        var chatId = moderator.getTelegramUserId();
        Long targetUserId = null;

        if(message.hasText() && message.getForwardFrom() == null) {


            var text = message.getText();

            String username = text.startsWith("@") ? text.substring(1) : text;

            try {

                targetUserId = appUserRepository.findByUsername(username)
                        .map(AppUser::getTelegramUserId).orElseThrow(() ->
                                new UserNotFoundException("❌ Пользователь не найден!"));

            } catch (UserNotFoundException ex) {

                messageSender.sendAnswer("❌ Неверный @username!", chatId);
                log.debug(ex.getMessage());
                return;
            }

        } else if(message.hasContact()){

            targetUserId = message.getContact().getUserId();

        } else if (message.getForwardFrom() != null) {

            targetUserId = message.getForwardFrom().getId();

        } else {
            messageSender.sendAnswer("❌ Неверный формат! Отправьте @username, контакт или перешлите сообщение.", chatId);
            return;
        }

        // 2. Ищем пользователя в БД
        var userOptional = appUserRepository.findAppUserByTelegramUserId(targetUserId);

        if (userOptional.isPresent()) {
            AppUser userToDelete = userOptional.get();
            String targetUsername = userToDelete.getUsername();

            // 3. Удаляем пользователя
            appUserRepository.delete(userToDelete);
            log.info("Модератор {} удалил пользователя @{}", moderator.getUsername(), targetUsername);

            messageSender.sendAnswer("✅ Пользователь @" + targetUsername + " успешно удален из системы.", chatId);
        } else {
            messageSender.sendAnswer("❌ Пользователь  не найден в базе данных.", chatId);
        }

        // 4. Возвращаем модератора в дефолтное состояние
        moderator.setUserState(UserState.IDLE);
        appUserRepository.save(moderator);
    }

    @Override
    public UserState getSupportedState() {
        return UserState.AWAITING_USER_FOR_DELETE;
    }
}