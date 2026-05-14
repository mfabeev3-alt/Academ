package ru.shatskikh.node.service.commands.callbackHandlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.node.service.commands.CallbackHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCallbackHandler implements CallbackHandler {

    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    @Override
    public boolean isSupported(String data) {
        return data.startsWith("registration_");
    }


    @Override
    public void handle(Update update, AppUser user) {

        var query = update.getCallbackQuery();
        var data = query.getData();
        var message = query.getMessage();
        var chatId = message.getChatId();
        var messageId = message.getMessageId();

        if (data != null) {

            String[] parts = data.split(":");

            if (parts[0].equals("registration_approve_user")) {

                Long approvedStudentId = Long.valueOf(parts[1]);

                AppUser approvedStudent = appUserRepository.findAppUserByTelegramUserId(approvedStudentId)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден!"));

                approvedStudent.setIsApproved(true);
                approvedStudent.setUserState(UserState.IDLE);

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

                approvedStudent.setUserState(UserState.IDLE);
                approvedStudent.setUserRole(UserRole.ROLE_STUDENT);
                approvedStudent.setTempData(null);
                appUserRepository.save(approvedStudent);

                String outputForStudent = "Регистрация прошла успешно! Теперь вы можете пользоваться меню.";
                messageSender.sendAnswerWithKeyboard(outputForStudent, approvedStudentId, keyboardMarkup);

                String outputForLeader = "Заявка одобрена!";
                messageSender.sendEditAnswer(chatId, message.getMessageId(),outputForLeader, null);

            } else if (parts[0].equals("registration_decline_user")) {

                Long declinedStudentId = Long.valueOf(user.getTempData());

                AppUser approvedStudent = appUserRepository.findAppUserByTelegramUserId(declinedStudentId)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден!"));

                approvedStudent.setUserState(UserState.IDLE);
                approvedStudent.setTempData(null);
                approvedStudent.setGroup(null);
                appUserRepository.save(approvedStudent);

                String outputForStudent = "Староста отклонил вашу заявку!";
                messageSender.sendAnswer(outputForStudent, declinedStudentId);

                String outputForLeader = "Заявка успешно отклонена!";
                InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(Collections.emptyList()).build();

                log.info("Отправляю правку сообщения {}", messageId);
                messageSender.sendEditAnswer(chatId, messageId, outputForLeader, markup);
            }

        } else {
            log.debug("Прилетел пустой Callback: " + data);
        }

    }


}
