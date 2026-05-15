package ru.shatskikh.node.service.commands.stateHandlerImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Faculty;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository;


@Component
@RequiredArgsConstructor
public class CreateFacultyStateHandler implements StateHandler {

    private final MessageSender messageSender;
    private final AppUserRepository appUserRepository;
    private final FacultyRepository facultyRepository;


    @Override
    public void handle(Update update, AppUser user) {

        var message = update.getMessage();
        var chatId = message.getChatId();
        var text = message.getText();


        facultyRepository.save(Faculty.builder().name(text).build());

        user.setUserState(UserState.IDLE);
        appUserRepository.save(user);

        messageSender.sendAnswer("✅ Факультет успешно создан!", chatId);
    }

    @Override
    public UserState getSupportedState() {
        return UserState.CREATING_FACULTY;
    }
}
