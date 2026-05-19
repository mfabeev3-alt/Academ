package ru.shatskikh.dispatcher.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.shatskikh.dispatcher.service.impl.UpdateProducerImpl;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    private final RabbitTemplate rabbitTemplate;
    private final UpdateProducerImpl producer;// Вместо контроллера!

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("Получен Update, отправляю в очередь...");
        // Отправляем сырой апдейт в очередь для входящих сообщений
        rabbitTemplate.convertAndSend("callback_update", update);
    }

    public void sendAnswerMessage(PartialBotApiMethod<?> message) {

        if (message == null) {
            return;
        }

        try {
            switch (message) {
                case SendMessage sendMessage -> execute(sendMessage);
                case org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText editMessageText ->
                        execute(editMessageText);
                case org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup editMessageReplyMarkup ->
                        execute(editMessageReplyMarkup);
                case org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption editMessageCaption ->
                        execute(editMessageCaption);
                case SendPhoto sendPhoto -> execute(sendPhoto);
                case SendDocument sendDocument -> execute(sendDocument);
                case SendVideo sendVideo -> execute(sendVideo);
                case org.telegram.telegrambots.meta.api.methods.send.SendAudio sendAudio -> execute(sendAudio);
                case org.telegram.telegrambots.meta.api.methods.send.SendAnimation sendAnimation ->
                        execute(sendAnimation);
                default -> throw new IllegalArgumentException(
                        "Неподдерживаемый тип сообщения: " + message.getClass().getName()
                );
            }

        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }
}

