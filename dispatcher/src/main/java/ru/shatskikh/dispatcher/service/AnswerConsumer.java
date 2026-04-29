package ru.shatskikh.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumer {
    void consume (SendMessage message);

}
