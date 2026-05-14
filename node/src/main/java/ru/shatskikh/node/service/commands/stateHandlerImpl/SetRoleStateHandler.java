package ru.shatskikh.node.service.commands.stateHandlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.node.exceptions.UserNotFoundException;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SetRoleStateHandler implements StateHandler {

    private final MessageSender messageSender;
    private final AppUserRepository appUserRepository;
    private final GroupRepository groupRepository;

    @Override
    public UserState getSupportedState() {
        return UserState.AWAITING_USER_FOR_ROLE;
    }

    @Override
    public void handle(Update update, AppUser admin) {

        var message = update.getMessage();
        var chatId = admin.getTelegramUserId();

        Long targetUserId = null;
        //check what admin sent

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

        String groupId = admin.getTempData();

        Group group = groupRepository.findById(Long.valueOf(groupId)).orElseThrow(()-> new EntityNotFoundException("Группа не найдена!"));

        appUserRepository.findAppUserByTelegramUserId(targetUserId).ifPresentOrElse(
                targetUser -> {
                    targetUser.setUserRole(UserRole.ROLE_LEADER);
                    targetUser.setGroup(group);
                   appUserRepository.save(targetUser);

                   admin.setUserState(UserState.IDLE);
                   admin.setTempData(null);
                   appUserRepository.save(admin);
                   messageSender.sendAnswer(
                           String.format("✅ Успешно! Пользователю @%s присвоена роль старосты!", targetUser.getUsername()),
                           chatId);

                       ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                       keyboardMarkup.setSelective(true);
                       keyboardMarkup.setResizeKeyboard(true);
                       keyboardMarkup.setOneTimeKeyboard(false);

                       List<KeyboardRow> keyboard = new ArrayList<>();

                       KeyboardRow row1 = new KeyboardRow();
                       row1.add("Какая сейчас пара?");

                       KeyboardRow row2 = new KeyboardRow();
                       row2.add("Расписание на неделю");

                       keyboard.add(row1);
                       keyboard.add(row2);

                       keyboardMarkup.setKeyboard(keyboard);

                       messageSender.sendAnswerWithKeyboard(
                               "✅ Вам присвоена роль старосты!", targetUser.getTelegramUserId(), keyboardMarkup);



                },
                () -> messageSender.sendAnswer("Пользователь не найден в базе данных!", chatId)
        );

    }

}
