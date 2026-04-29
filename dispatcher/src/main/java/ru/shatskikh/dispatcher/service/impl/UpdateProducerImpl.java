package ru.shatskikh.dispatcher.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.dispatcher.service.UpdateProducer;


@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug("Отправка в очередь {}: {}", rabbitQueue, update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
