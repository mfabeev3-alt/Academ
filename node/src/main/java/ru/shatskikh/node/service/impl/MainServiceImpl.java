package ru.shatskikh.node.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.shatskikh.entity.AppDocument;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.node.entity.RawData;
import ru.shatskikh.node.repositories.RawDataRepository;
import ru.shatskikh.node.service.CallbackHandler;
import ru.shatskikh.node.service.FileService;
import ru.shatskikh.node.service.MainService;
import ru.shatskikh.node.service.ProducerService;
import ru.shatskikh.node.service.commands.dispatcherImpl.CallbackDispatcher;
import ru.shatskikh.node.service.commands.dispatcherImpl.CommandDispatcher;
import ru.shatskikh.node.service.commands.dispatcherImpl.StateDispatcher;
import ru.shatskikh.node.utils.MenuCommandMapper;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

import java.util.Optional;

import static ru.shatskikh.entity.enums.UserRole.*;
import static ru.shatskikh.entity.enums.UserState.IDLE;

@Service
@Slf4j
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;
    private final FileService fileService;
    private final MessageSender messageSender;
    private final CallbackHandler callbackHandler;
    private final CommandDispatcher commandDispatcher;
    private final CallbackDispatcher callbackDispatcher;
    private final StateDispatcher stateDispatcher;
    private final MenuCommandMapper menuCommandMapper;

    @Override
    public void processTextMessage(Update update) {

        saveRawData(update);

        var message = update.getMessage();
        var text = message.getText();
        var chatId = message.getChatId();
        var user = findOrSaveAppUser(update);

        String processedText = menuCommandMapper.map(text);

        if (processedText != null && processedText.startsWith("/")) {
            commandDispatcher.dispatch(update, user);
            return;
        }

        if(user.getUserState() != IDLE){
            stateDispatcher.dispatch(update, user);
        }

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);

        var message = update.getMessage();
        var chatId = message.getChatId();
        var appUser = findOrSaveAppUser(update);


        //TODO Add doc saving

        var answer = "Document was saved successfully! Link for downloading: ";

        messageSender.sendAnswer(answer, chatId);

    }

    @Override
    public void processCallbackQuery(Update update) {

        var user = findOrSaveAppUser(update);
        callbackDispatcher.dispatch(update, user);

    }


    @Override
    public void processDocMessage(Update update) {

        saveRawData(update);
        var message = update.getMessage();
        var chatId = message.getChatId();
        var appUser = findOrSaveAppUser(update);

        AppDocument persistedAppDoc = fileService.processFile(message);

        var answer ="";
        if (persistedAppDoc != null) {
            answer = "Document was saved successfully! Link for downloading: "; }
        else {
            answer = "Smth went wrong";
        }

    }


    private String help() {
            return "List of command:\n"
                    + "/cancel – undoing previous command;\n"
                    + "/registration – user registration.";
    }


    private String cancelProcess(AppUser appUser) {

        appUser.setUserRole(ROLE_GUEST);
        appUserRepository.save(appUser);
        return "Command is canceled!";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();

        rawDataRepository.save(rawData);

    }

    private AppUser findOrSaveAppUser(Update update) {

        User telegramUser = null;

        if (update.hasMessage()) {
            telegramUser = update.getMessage().getFrom();
        } else if (update.hasCallbackQuery()) {
            telegramUser = update.getCallbackQuery().getFrom();
        }

        long telegramId = telegramUser.getId();
        Optional<AppUser> persistentAppUser = appUserRepository.findAppUserByTelegramUserId(telegramId);

        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramId)
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .username(telegramUser.getUserName())
                    .isApproved(false)
                    .userRole(ROLE_GUEST)
                    .userState(IDLE)
                    .build();

            return appUserRepository.save(transientAppUser); //метод save возвращает сохранённый объект с первичным ключом и привязкой к сессии Hibernate

        }

        return persistentAppUser.get();
    }

}
