package ru.shatskikh.node.service.commands.stateHandlerImpl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.shatskikh.entity.enums.UserState.AWAITING_REGISTRATION;
import static ru.shatskikh.node.utils.enums.Buttons.APPROVE_USER;
import static ru.shatskikh.node.utils.enums.Buttons.DECLINE_USER;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationStateHandler implements StateHandler {

    private final AppUserRepository appUserRepository;
    private final GroupRepository groupRepository;
    private final MessageSender messageSender;

    @Override
    public UserState getSupportedState() {
        return AWAITING_REGISTRATION;
    }

    @Override
    public void handle(Update update, AppUser user) {

        var message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();

        switch (user.getTempData()) {

            case "awaiting_fio" -> processFioStep(text, user, chatId);
            case "awaiting_group" -> processGroupStep(text, user, chatId);
            case "pending_approval" ->
                    messageSender.sendAnswer("Ваша заявка ещё на рассмотрении у старосты.", chatId);
            default ->
                    messageSender.sendAnswer("Чтобы начать регистрацию, введите /registration", chatId);
        }

    }

    private void processFioStep(String fio, AppUser user, Long chatId) {
        user.setFio(fio);
        user.setTempData("awaiting_group");
        appUserRepository.save(user);
        log.info("User " + user.getUsername() +" is " + user.getUserState().toString());
        messageSender.sendAnswer("Принято! Теперь введите номер вашей группы (например, ОП341).", chatId);

    }

    private void processGroupStep(String group, AppUser user, Long chatId) {

        Optional<Group> groupOut = groupRepository.findByName(group);

        if(groupOut.isEmpty()){
            messageSender.sendAnswer("Группа " + group + " не найдена! Уточните название.", chatId);
            return;
        }

        user.setGroup(groupOut.get());
        user.setTempData("pending_approval");
        appUserRepository.save(user);

        messageSender.sendAnswer("Данные переданы старосте группы. Ожидайте подтверждения!", chatId);
        log.debug("User " + user.getUsername() +" is " + user.getUserState().toString());

        notifyLeader(user, groupOut.get());
    }

    private void notifyLeader(AppUser student, Group group ) {

        List<AppUser> leaders = appUserRepository.findAppUserByUserRoleAndGroup(UserRole.ROLE_LEADER, group);

        if(leaders.isEmpty()) {

            String output = "Староста в вашей группе ещё не назначен!";
            student.setTempData(null);
            student.setUserState(UserState.IDLE);
            appUserRepository.save(student);
            messageSender.sendAnswer(output, student.getTelegramUserId());

            return;
        }

        for(AppUser leader: leaders) {

            String messageText = String.format(
                    "Новая заявка на вступление!\n\n" +
                            "Студент: %s\n\n" +
                            "Группа: %s\n\n" +
                            "Username: @%s",
                    student.getFio(), group.getName(), student.getUsername()
            );

            buildAndSendMessage(messageText, leader.getTelegramUserId(), student.getTelegramUserId());
        }
    }

    private void buildAndSendMessage(String output, Long leaderChatId, Long studentId){

        var sendMessage = new SendMessage();
        sendMessage.setChatId(leaderChatId);
        sendMessage.setText(output);

        var markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                InlineKeyboardButton.builder().text("Подтвердить").callbackData(APPROVE_USER.getValue() + ":" + studentId).build(),
                InlineKeyboardButton.builder().text("Отклонить").callbackData(DECLINE_USER.getValue() + ":" + studentId).build()
        ));

        markup.setKeyboard(rows);

        messageSender.sendAnswerWithKeyboard(output, leaderChatId, markup);
    }

}
