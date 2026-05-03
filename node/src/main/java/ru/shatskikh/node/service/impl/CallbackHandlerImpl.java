package ru.shatskikh.node.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.exceptions.UserNotFoundException;
import ru.shatskikh.node.service.CallbackHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;

import java.util.Objects;

import static ru.shatskikh.node.utils.enums.Buttons.DECLINE_USER;

@Slf4j
@Service
public class CallbackHandlerImpl implements CallbackHandler {


    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    @Autowired
    public CallbackHandlerImpl(AppUserRepository appUserRepository, MessageSender messageSender) {
        this.appUserRepository = appUserRepository;
        this.messageSender = messageSender;
    }

    @Override
    public void handle(CallbackQuery query) {
        String callbackData = query.getData();
        long leaderChatId = query.getMessage().getChatId();
        long msgId = query.getMessage().getMessageId();

        if (callbackData.startsWith("approve_user")) {
                Long studentId = Long.parseLong(callbackData.split(":")[1]);

                processApproval(leaderChatId, msgId, studentId);


        } else if (Objects.equals(callbackData.split(":")[0], DECLINE_USER.getValue())) {


        }

    }

    private void processApproval(long leaderChatId, long msgId, Long studentId) {

        AppUser student = appUserRepository.findAppUserByTelegramUserId(studentId).
                       orElseThrow(() -> new UserNotFoundException("User not found!"));


        student.setUserState(UserState.IDLE);
        student.setUserRole(UserRole.ROLE_STUDENT);
        student.setIsApproved(true);

        appUserRepository.save(student);

        messageSender.sendAnswer("Староста подтвердил ваши права! Добро пожаловать!", studentId);


    }


}
