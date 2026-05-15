package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteGroupCommand implements BotCommand {

    private final MessageSender messageSender;
    private final GroupRepository groupRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public String getCommandIdentifier() {
        return "/delete_group";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_ADMIN;
    }

    @Override
    public void execute(Update update, AppUser admin) {

        var message = update.getMessage();
        var chatId = message.getChatId();
        var messageId = message.getMessageId();

        admin.setUserState(UserState.AWAITING_GROUP_FOR_DELETE);
        appUserRepository.save(admin);

        sendGroupList(chatId, messageId);

    }

    private void sendGroupList(Long chatId, Integer messageId) {


        List<Group> groups = groupRepository.findAll();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for(Group group: groups){

            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(group.getName())
                            .callbackData("dg_" + group.getId())
                            .build()
            ));

        }

        markup.setKeyboard(rows);
        messageSender.sendAnswerWithKeyboard("\uD83C\uDFDA Выберите группу, которую вы хотите удалить.",chatId, markup);
    }


}
