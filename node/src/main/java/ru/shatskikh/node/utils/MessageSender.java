package ru.shatskikh.node.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shatskikh.node.service.ProducerService;

import java.util.ArrayList;
import java.util.List;

import static ru.shatskikh.node.utils.enums.Buttons.DECLINE_USER;
import static ru.shatskikh.node.utils.enums.Buttons.APPROVE_USER;

@Component
public class MessageSender {

    private final ProducerService producerService;

    @Autowired
    public MessageSender(ProducerService producerService) {
        this.producerService = producerService;
    }

    public void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    public void sendAnswerWithApprovalKeyboard(String output, Long chatId) {

        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);

        var markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();
        yesButton.setText(APPROVE_USER.getValue());
        yesButton.setCallbackData(APPROVE_USER.name());

        var noButton = new InlineKeyboardButton();
        noButton.setText(DECLINE_USER.getValue() + ":" + chatId);
        noButton.setCallbackData(DECLINE_USER.name() + ":" + chatId);

        rowInline.add(yesButton);
        rowInline.add(noButton);

        rowsInline.add(rowInline);

        markup.setKeyboard(rowsInline);

        sendMessage.setReplyMarkup(markup);
    }

}
