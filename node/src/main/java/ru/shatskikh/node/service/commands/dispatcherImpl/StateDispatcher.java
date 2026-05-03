package ru.shatskikh.node.service.commands.dispatcherImpl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.Dispatcher;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StateDispatcher implements Dispatcher {

    private final Map<UserState, StateHandler> allHandlers;
    private final MessageSender messageSender;

    @Autowired
    public StateDispatcher(List<StateHandler> allHandlers, MessageSender messageSender) {
        this.allHandlers = allHandlers.stream()
                .collect(Collectors.toMap(StateHandler::getSupportedState, handler -> handler ));
        this.messageSender = messageSender;
    }

    @Override
    public void dispatch(Update update, AppUser user) {

        StateHandler handler = allHandlers.get(user.getUserState());

        if(handler != null) {
            handler.handle(update, user);
            log.debug("State Dispatcher got a state: " + user.getUserState().toString());
        } else {

            //TODO anilyze possible errors
            log.error("There is state, but not handler");
        }

    }
}
