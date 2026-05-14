package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Faculty;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateNewGroup implements BotCommand {

    private final MessageSender messageSender;
    private final FacultyRepository facultyRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public String getCommandIdentifier() {
        return "/create_new_group";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_MODERATOR;
    }

    @Override
    public void execute(Update update, AppUser user) {

        var chatId = update.getMessage().getChatId();

        user.setUserState(UserState.CREATING_GROUP);
        appUserRepository.save(user);

        sendFacultyList(chatId);
    }

    private void sendFacultyList(Long chatId) {

        List<Faculty> faculties = facultyRepository.findAll();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for(Faculty faculty: faculties){

            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(faculty.getName())
                            .callbackData("gr_" + faculty.getId())
                            .build()
            ));

        }

        markup.setKeyboard(rows);
        messageSender.sendAnswerWithKeyboard("\uD83C\uDFDA Выберите факультет", chatId, markup);

    }


}
