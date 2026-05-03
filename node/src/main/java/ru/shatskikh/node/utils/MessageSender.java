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


    public void sendAnswerWithKeyboard(String output, Long leaderChatId, InlineKeyboardMarkup markup) {

        var sendMessage = new SendMessage();
        sendMessage.setChatId(leaderChatId);
        sendMessage.setText(output);

        sendMessage.setReplyMarkup(markup);
        producerService.produceAnswer(sendMessage);
    }

}
