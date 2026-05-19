package ru.shatskikh.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import ru.shatskikh.model.MenuUpdateDto;


public interface AnswerConsumer {
    void consume (PartialBotApiMethod<?> method);
    void consumeWebApp (MenuUpdateDto dto);
}
