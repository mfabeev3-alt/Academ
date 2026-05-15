package ru.shatskikh.node.service.commands.callbackHandlerImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.node.service.commands.CallbackHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.GroupRepository;


@Component
@RequiredArgsConstructor
public class DeleteGroupCallbackHandler implements CallbackHandler {

    private final MessageSender messageSender;
    private final GroupRepository groupRepository;
    private final AppUserRepository appUserRepository;


    @Override
    public void handle(Update update, AppUser user) {

        var query = update.getCallbackQuery();
        var data = query.getData();
        var chatId = query.getMessage().getChatId();
        var messageId = query.getMessage().getMessageId();

        data = data.replace("dg_", "");

        groupRepository.deleteById(Long.valueOf(data));

        messageSender.sendEditAnswer(chatId, messageId,
                "✅ Группа успешно удалена! ", null);
    }

    @Override
    public boolean isSupported(String data) {
        return data != null && data.startsWith("dg_");
    }
}
