package ru.shatskikh.node.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.shatskikh.model.MenuUpdateDto;
import ru.shatskikh.node.service.ProducerService;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import java.util.List;

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

    public void sendAnswer(Update update, Long chatId) {

        Message message = update.getMessage();

        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());

        if(message.hasPhoto()) {

            List<PhotoSize> photos = message.getPhoto();
            PhotoSize photo = photos.getLast();

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(photo.getFileId()));

            if(message.getCaption() != null) {

                sendPhoto.setCaption(message.getCaption());

            }

            producerService.produceAnswer(sendPhoto);

        }

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

    public void setPersonalizedCommands(Long chatId, List<BotCommand> commands) {

        BotCommandScopeChat scope = new BotCommandScopeChat();
        scope.setChatId(chatId.toString());

        SetMyCommands setMyCommands = new SetMyCommands(commands, scope, null);

        producerService.produceAnswer(setMyCommands);

    }

    public void setMenu(MenuUpdateDto dto) {

        producerService.produceWebApp(dto);

    }

}
