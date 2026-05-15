package ru.shatskikh.node.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextMessageUpdated(Update update);

    void consumeCallbackQueryUpdate(Update update);
}
