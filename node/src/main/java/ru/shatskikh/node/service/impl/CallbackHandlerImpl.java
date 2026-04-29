package ru.shatskikh.node.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.shatskikh.node.service.CallbackHandler;
import ru.shatskikh.repository.AppUserRepository;

import java.util.Objects;

import static ru.shatskikh.node.utils.enums.Buttons.DECLINE_USER;
import static ru.shatskikh.node.utils.enums.Buttons.APPROVE_USER;

@Service
public class CallbackHandlerImpl implements CallbackHandler {


    private final AppUserRepository appUserRepository;

    @Autowired
    public CallbackHandlerImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void handle(CallbackQuery query) {
        String callbackData = query.getData();
        long chatId = query.getMessage().getChatId();
        long msgId = query.getMessage().getMessageId();

        if (Objects.equals(callbackData, APPROVE_USER.name())) {




        } else if (Objects.equals(callbackData, DECLINE_USER.name())) {

        }

    }


}
