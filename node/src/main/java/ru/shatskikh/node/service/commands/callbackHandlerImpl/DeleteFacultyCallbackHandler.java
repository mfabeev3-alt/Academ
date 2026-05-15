package ru.shatskikh.node.service.commands.callbackHandlerImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.node.service.commands.CallbackHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository;

@Component
@RequiredArgsConstructor
public class DeleteFacultyCallbackHandler implements CallbackHandler {

    private final MessageSender messageSender;
    private final FacultyRepository facultyRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public void handle(Update update, AppUser user) {
        var query = update.getCallbackQuery();
        var data = query.getData();
        var chatId = query.getMessage().getChatId();
        var messageId = query.getMessage().getMessageId();

        // Извлекаем ID факультета, убирая префикс df_
        String facultyId = data.replace("df_", "");

        facultyRepository.deleteById(Long.valueOf(facultyId));

        messageSender.sendEditAnswer(chatId, messageId,
                "✅ Факультет успешно удален!", null);
    }

    @Override
    public boolean isSupported(String data) {
        // Проверяем, что callback пришел именно от кнопок удаления факультета
        return data != null && data.startsWith("df_");
    }
}