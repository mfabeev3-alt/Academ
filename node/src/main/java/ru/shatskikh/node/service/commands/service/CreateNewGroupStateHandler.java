package ru.shatskikh.node.service.commands.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Faculty;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository;
import ru.shatskikh.repository.GroupRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class CreateNewGroupStateHandler implements StateHandler {

    private final MessageSender messageSender;
    private final AppUserRepository appUserRepository;
    private final FacultyRepository facultyRepository;
    private final GroupRepository groupRepository;

    @Override
    public void handle(Update update, AppUser user) {


        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var message = update.getMessage();
        var chatId = message.getChatId();
        var text = message.getText();
        var messageId = message.getMessageId();

        // Защита: вдруг админ еще не нажал кнопку, а уже пишет текст
        if (user.getTempData() == null) {
            messageSender.sendAnswer("⚠️ Сначала выберите факультет!", chatId);
            return;
        }


        Faculty faculty = facultyRepository.findById(Long.valueOf(user.getTempData()))
                .orElseThrow(() -> new RuntimeException("Факультет не найден"));

        // Парсим названия групп
        List<String> groupNames = Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .toList();

        if (groupNames.isEmpty()) {
            messageSender.sendAnswer("⚠️ Не удалось распознать названия групп. Попробуйте снова.", chatId);
            return;
        }

        int currentYear = LocalDate.now().getYear();


        List<Group> newGroups = groupNames.stream()
                .map(name -> Group.builder()
                        .name(name)
                        .faculty(faculty)
                        .entryYear(currentYear) // Заполняем год поступления!
                        .build())
                .toList();

        groupRepository.saveAll(newGroups);

        user.setTempData(null);
        user.setUserState(UserState.IDLE); // Переводим обратно в базовое состояние
        appUserRepository.save(user);

        messageSender.sendAnswer(
                "🎉 Успешно создано " + newGroups.size() + " групп(ы) " +
                        "для факультета «" + faculty.getName() + "»!",
                chatId
        );

    }

    @Override
    public UserState getSupportedState() {
        return UserState.CREATING_GROUP;
    }
}
