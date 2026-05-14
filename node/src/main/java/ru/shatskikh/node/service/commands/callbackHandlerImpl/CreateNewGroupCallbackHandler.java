package ru.shatskikh.node.service.commands.callbackHandlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.node.service.commands.CallbackHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateNewGroupCallbackHandler implements CallbackHandler {

        private final MessageSender messageSender;
        private final AppUserRepository appUserRepository;

        @Override
        public void handle(Update update, AppUser user) {

            var callbackQuery = update.getCallbackQuery();
            var data = callbackQuery.getData();
            var chatId = callbackQuery.getMessage().getChatId();
            var messageId = callbackQuery.getMessage().getMessageId();

            Long facultyId = Long.parseLong(data.replace("gr_", ""));

            // 2. Сохраняем ID факультета во временное поле пользователя
            user.setTempData(String.valueOf(facultyId));
            appUserRepository.save(user); // Обязательно сохраняем изменения в БД

            // 3. Даем инструкцию для следующего шага
            messageSender.sendEditAnswer(chatId, messageId,
                    "✅ Факультет выбран!\n\n" +
                            "Теперь отправьте названия новых групп через запятую.\n" +
                            "Например: ОП341, СИ123, ОС543",
                    null
            );
        }

        @Override
        public boolean isSupported(String data) {
            // Хендлер срабатывает только если дата начинается с нужного префикса
            return data != null && data.startsWith("gr_");
        }


}

