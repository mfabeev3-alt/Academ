package ru.shatskikh.dispatcher.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.dispatcher.service.UpdateProducer;
import ru.shatskikh.dispatcher.utils.MessageUtils;

import static ru.shatskikh.config.RabbitConfiguration.*;


@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateController {
    private final RabbitTemplate rabbitTemplate;
    private final MessageUtils messageUtils;
    private final UpdateProducer producer;

    @RabbitListener(queues = "callback_update") // Контроллер сам слушает очередь!
    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null!");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);

        } else if (update.hasCallbackQuery()) {

            processCallbackQuery(update);

        } else {
            log.error("Received unsupported message type " + update);
        }

    }

    private void distributeMessagesByType(Update update) {

        var msg = update.getMessage();

        if (msg.hasText()) {
            processTextMessage(update);
        } else  {
            setUnsupportedMessageTypeView(update);
        }

    }

    private void processCallbackQuery(Update update) {
        producer.produce(CALLBACK_QUERY_UPDATE, update);
    }



    private void processTextMessage(Update update) {
        producer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    private void setFileReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "File received! Processing...");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("answer_message", sendMessage);
    }

}
