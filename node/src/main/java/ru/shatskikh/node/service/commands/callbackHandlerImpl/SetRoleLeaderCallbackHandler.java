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
@Slf4j
@RequiredArgsConstructor
public class SetRoleLeaderCallbackHandler implements CallbackHandler {


    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    @Override
    public void handle(Update update, AppUser user) {

        var query = update.getCallbackQuery();
        var data = query.getData();
        var chatId = query.getMessage().getChatId();
        var messageId = query.getMessage().getMessageId();

        data = data.replace("ld_", "");

        user.setTempData(data);
        appUserRepository.save(user);

        messageSender.sendEditAnswer(chatId, messageId,
                "✅ Теперь отправьте @username, контакт или перешлите сообщение пользователя," +
                        "которого вы хотите наделить правами старосты. ", null);

    }

    @Override
    public boolean isSupported(String data) {
        return data != null && data.startsWith("ld_");
    }
}
