package ru.shatskikh.node.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.shatskikh.node.service.ProducerService;

@Component
@RequiredArgsConstructor
public class MessageSender {

    private final ProducerService producerService;

    public void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    public void sendAnswerWithKeyboard(String output, Long leaderChatId, ReplyKeyboard markup) {

        var sendMessage = new SendMessage();
        sendMessage.setChatId(leaderChatId);
        sendMessage.setText(output);

        sendMessage.setReplyMarkup(markup);
        producerService.produceAnswer(sendMessage);
    }

    public void sendEditAnswer(Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) {

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text)
                .replyMarkup(markup)
                .build();

         producerService.produceAnswer(editMessageText);
    }

}
