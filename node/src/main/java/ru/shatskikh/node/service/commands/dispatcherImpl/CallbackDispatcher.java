package ru.shatskikh.node.service.commands.dispatcherImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.node.service.commands.CallbackHandler;
import ru.shatskikh.node.service.commands.Dispatcher;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CallbackDispatcher implements Dispatcher {


    private final List<CallbackHandler> handlers;


    @Override
    public void dispatch(Update update, AppUser user) {

        String data = update.getCallbackQuery().getData();

        for (CallbackHandler handler: handlers) {

            if(handler.isSupported(data)) {

                handler.handle(update, user);
                return;
            }

        }

        log.debug("Обработчик не найден");

    }
}
