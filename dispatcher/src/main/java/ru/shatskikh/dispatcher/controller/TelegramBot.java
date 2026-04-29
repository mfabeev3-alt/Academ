package ru.shatskikh.dispatcher.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.shatskikh.dispatcher.service.impl.UpdateProducerImpl;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}") private String botName;
    @Value("${bot.token}") private String botToken;

    private final RabbitTemplate rabbitTemplate;
    private final UpdateProducerImpl producer;// Вместо контроллера!

    @Override
    public String getBotUsername() { return botName; }
    @Override
    public String getBotToken() { return botToken; }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("Получен Update, отправляю в очередь...");
        // Отправляем сырой апдейт в очередь для входящих сообщений
        rabbitTemplate.convertAndSend("callback_update", update);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Ошибка при физической отправке: " + e.getMessage());
            }
        }
    }
}
