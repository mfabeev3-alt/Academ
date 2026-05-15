package ru.shatskikh.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.shatskikh.model.MenuUpdateDto;


public interface AnswerConsumer {
    void consume (BotApiMethod<?> method);
    void consumeWebApp (MenuUpdateDto dto);
}
