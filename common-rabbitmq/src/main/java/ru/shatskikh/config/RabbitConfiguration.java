package ru.shatskikh.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

        public static final String TEXT_MESSAGE_UPDATE = "text_message_update";
        public static final String DOC_MESSAGE_UPDATE = "doc_message_update";
        public static final String PHOTO_MESSAGE_UPDATE = "photo_message_update";
        public static final String ANSWER_MESSAGE = "answer_message";
        public static final String CALLBACK_UPDATE = "callback_update";
        public static final String CALLBACK_QUERY_UPDATE ="callback_query_update";}
