package ru.shatskikh.dispatcher.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButton;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonDefault;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonWebApp;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import ru.shatskikh.dispatcher.controller.TelegramBot;
import ru.shatskikh.dispatcher.service.AnswerConsumer;
import ru.shatskikh.model.MenuUpdateDto;


import static ru.shatskikh.config.RabbitConfiguration.ANSWER_MESSAGE;
import static ru.shatskikh.config.RabbitConfiguration.ANSWER_MESSAGE_WEB_APP;

@Service
@RequiredArgsConstructor

public class AnswerConsumerImpl implements AnswerConsumer {
    private final TelegramBot telegramBot;

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(PartialBotApiMethod<?> message) {telegramBot.sendAnswerMessage(message);}

    @Override
    @RabbitListener(queues =  ANSWER_MESSAGE_WEB_APP)
    public void consumeWebApp(MenuUpdateDto dto) {

        MenuButton button;

        if(dto.getUrl() == null || dto.getUrl().isEmpty()){
            button = MenuButtonDefault.builder().build();

        } else {

            button = MenuButtonWebApp.builder()
                    .text(dto.getText())
                    .webAppInfo(WebAppInfo.builder().url(dto.getUrl()).build())
                    .build();
        }

        SetChatMenuButton method = SetChatMenuButton.builder()
                .chatId(dto.getUserId().toString())
                .menuButton(button)
                .build();

        telegramBot.sendAnswerMessage(method);

    }

}