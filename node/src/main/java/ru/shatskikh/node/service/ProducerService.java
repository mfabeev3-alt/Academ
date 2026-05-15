package ru.shatskikh.node.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import ru.shatskikh.model.MenuUpdateDto;

public interface ProducerService {
    void produceAnswer(BotApiMethod<?> message);
    void produceWebApp(MenuUpdateDto dto);
}
