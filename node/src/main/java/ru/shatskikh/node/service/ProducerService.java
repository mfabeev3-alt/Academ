package ru.shatskikh.node.service;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import ru.shatskikh.model.MenuUpdateDto;

public interface ProducerService {
    void produceAnswer(PartialBotApiMethod<?> message);
    void produceWebApp(MenuUpdateDto dto);
}
