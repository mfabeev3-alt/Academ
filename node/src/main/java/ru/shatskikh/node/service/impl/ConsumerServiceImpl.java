package ru.shatskikh.node.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.node.service.ConsumerService;
import ru.shatskikh.node.service.MainService;
import ru.shatskikh.node.service.ProducerService;

import static ru.shatskikh.config.RabbitConfiguration.*;


@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    @Autowired
    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdated(Update update) {
        log.debug("NODE: Принял текстовое сообщение");
        mainService.processTextMessage(update);

    }

    @Override
    @RabbitListener(queues = CALLBACK_QUERY_UPDATE)
    public void consumeCallbackQueryUpdate(Update update) {
        log.debug("NODE: Принял Callback Query");
        mainService.processCallbackQuery(update);
    }

}
