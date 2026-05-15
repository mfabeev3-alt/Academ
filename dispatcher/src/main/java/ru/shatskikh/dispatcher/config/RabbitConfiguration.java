package ru.shatskikh.dispatcher.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static ru.shatskikh.config.RabbitConfiguration.*;

@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter () {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public org.springframework.amqp.core.Queue callbackMessageQueue() {
        return new org.springframework.amqp.core.Queue(CALLBACK_UPDATE);
    }

    @Bean
    public org.springframework.amqp.core.Queue callbackQueryMessageQueue() {
        return new org.springframework.amqp.core.Queue(CALLBACK_QUERY_UPDATE);
    }

    @Bean
    public org.springframework.amqp.core.Queue textMessageQueue() {
        return new org.springframework.amqp.core.Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public org.springframework.amqp.core.Queue textAnswerMessageWebAppQueue() {
        return new org.springframework.amqp.core.Queue(ANSWER_MESSAGE_WEB_APP);
    }

    @Bean
    public org.springframework.amqp.core.Queue answerMessageQueue() {
        return new org.springframework.amqp.core.Queue(ANSWER_MESSAGE);
    }


}
