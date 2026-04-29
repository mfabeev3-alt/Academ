package ru.shatskikh.node.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.shatskikh.entity.AppDocument;

public interface FileService {
    AppDocument processFile(Message externalMessage);
}
