package ru.shatskikh.node.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.shatskikh.config.RabbitConfiguration.*;
import static ru.shatskikh.config.RabbitConfiguration.ANSWER_MESSAGE;
import static ru.shatskikh.config.RabbitConfiguration.TEXT_MESSAGE_UPDATE;

@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {return new Jackson2JsonMessageConverter();}

}
