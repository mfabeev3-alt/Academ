package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Faculty; // Предполагаем наличие этой сущности
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository; // Твой репозиторий для факультетов

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteFacultyCommand implements BotCommand {

    private final MessageSender messageSender;
    private final FacultyRepository facultyRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public String getCommandIdentifier() {
        return "/delete_faculty";
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

        // Устанавливаем состояние ожидания удаления факультета
        admin.setUserState(UserState.AWAITING_FACULTY_FOR_DELETE);
        appUserRepository.save(admin);

        sendFacultyList(chatId, messageId);
    }

    private void sendFacultyList(Long chatId, Integer messageId) {
        List<Faculty> faculties = facultyRepository.findAll();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Faculty faculty : faculties) {
            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(faculty.getName())
                            .callbackData("df_" + faculty.getId()) // Префикс df_ (delete faculty)
                            .build()
            ));
        }

        markup.setKeyboard(rows);
        messageSender.sendAnswerWithKeyboard("🏛 Выберите факультет, который вы хотите удалить.", chatId, markup);
    }
}