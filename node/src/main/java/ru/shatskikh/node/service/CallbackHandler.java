package ru.shatskikh.node.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {
    void handle(CallbackQuery query);
}
