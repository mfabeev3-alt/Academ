package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BroadcastCommand implements BotCommand {

    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;


    @Override
    public String getCommandIdentifier() {
        return "/broadcast";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_LEADER;
    }

    @Override
    public void execute(Update update, AppUser user) {

        var message = update.getMessage();
        var chaId = message.getChatId();

        user.setUserState(UserState.AWAITING_BROADCAST_CONTENT);
        appUserRepository.save(user);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                InlineKeyboardButton.builder().text("По группе").callbackData("bc_group").build()
        ));


        if (user.getUserRole().getLevel() >= UserRole.ROLE_MODERATOR.getLevel() ) {
            rows.add(List.of(
                    InlineKeyboardButton.builder().text("По факултету").callbackData("bc_faculty").build()
            ));

        }

       if (user.getUserRole().getLevel() == UserRole.ROLE_ADMIN.getLevel() ) {
           rows.add(List.of(
                   InlineKeyboardButton.builder().text("По корпусу").callbackData("bc_all").build(),
                   InlineKeyboardButton.builder().text("По академии").callbackData("bc_all").build()
           ));

       }

        markup.setKeyboard(rows);

        messageSender.sendAnswerWithKeyboard("Выберете уровень рассылки:", chaId, markup);

    }
}
