package ru.shatskikh.dispatcher.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.shatskikh.dispatcher.controller.TelegramBot;
import ru.shatskikh.dispatcher.service.AnswerConsumer;

import static ru.shatskikh.config.RabbitConfiguration.ANSWER_MESSAGE;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {
    private final TelegramBot telegramBot;

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(BotApiMethod<?> message) {
        telegramBot.sendAnswerMessage(message);
    }
}