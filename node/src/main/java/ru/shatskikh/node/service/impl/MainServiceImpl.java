package ru.shatskikh.node.service.impl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppDocument;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.entity.RawData;
import ru.shatskikh.node.repositories.RawDataRepository;
import ru.shatskikh.node.service.CallbackHandler;
import ru.shatskikh.node.service.FileService;
import ru.shatskikh.node.service.MainService;
import ru.shatskikh.node.service.ProducerService;
import ru.shatskikh.node.service.commands.CommandDispatcher;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

import static ru.shatskikh.entity.enums.UserRole.*;
import static ru.shatskikh.entity.enums.UserState.IDLE;
import static ru.shatskikh.node.service.enums.ServiceCommands.*;

@Service
@Slf4j
public class MainServiceImpl implements MainService {

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;
    private final FileService fileService;
    private final MessageSender messageSender;
    private final CommandDispatcher commandDispatcher;
    private final CallbackHandler callbackHandler;

    @Autowired
    public MainServiceImpl(RawDataRepository rawDataRepository, ProducerService producerService, AppUserRepository appUserRepository, FileService fileService, MessageSender messageSender, CommandDispatcher commandDispatcher, CallbackHandler callbackHandler) {
        this.rawDataRepository = rawDataRepository;
        this.producerService = producerService;
        this.appUserRepository = appUserRepository;
        this.fileService = fileService;
        this.messageSender = messageSender;
        this.commandDispatcher = commandDispatcher;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void processTextMessage(Update update) {

        saveRawData(update);

        var message = update.getMessage();
        var text = message.getText();
        var chatId = message.getChatId();


        var user = findOrSaveAppUser(update);
        var userRole = user.getUserRole();

        var output = "";


        if (text.startsWith("/")) {
            commandDispatcher.executeCommand(update, user);
            return;
        }

        if(user.getUserState() != IDLE){


        }

        messageSender.sendAnswer(output, chatId);
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

        callbackHandler.handle(update.getCallbackQuery());

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

        sendAnswer(answer, chatId);
    }


    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {

        if(REGISTRATION.equals(cmd)){
            //TODO add registration



            return "Temporary unenabled ";
        } else if(HELP.equals(cmd)) {
            return help();
        } else if(START.equals(cmd)) {
            return "Greetings! Type /help to watch command list";
        }   else {
            return "Unknown command! Type /help to watch command list";
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

        var telegramUser = update.getMessage().getFrom();
        long telegramId = telegramUser.getId();
        AppUser persistentAppUser = appUserRepository.findAppUserByTelegramUserId(telegramId);

        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramId)
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO ИЗМЕНИТЬ ЗНАЧЕННИЕ ПО УМОЛЧАНИЮ ПОСЛЕ ДОБАВЛЕНИЯ РЕГИСТРАЦИИ
                    .isApproved(true)
                    .userRole(ROLE_GUEST)
                    .userState(IDLE)
                    .build();

            return appUserRepository.save(transientAppUser); //метод save возвращает сохранённый объект с первичным ключом и привязкой к сессии Hibernate

        }

        return persistentAppUser;
    }

}
