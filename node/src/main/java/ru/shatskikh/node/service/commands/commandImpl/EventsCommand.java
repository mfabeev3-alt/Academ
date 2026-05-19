package ru.shatskikh.node.service.commands.commandImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Event;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.node.service.commands.BotCommand;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventsCommand implements BotCommand {

    private final EventRepository eventRepository;
    private final MessageSender messageSender;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public String getCommandIdentifier() {
        return "/events";
    }

    @Override
    public UserRole getRequiredRole() {
        return UserRole.ROLE_STUDENT;
    }

    @Override
    public void execute(Update update, AppUser user) {

        Long chatId = update.getMessage().getChatId();

        // Сначала проверяем, привязан ли пользователь к группе
        if (user.getGroup() == null) {
            messageSender.sendAnswer(
                    "❌ Вы не можете посмотреть мероприятия, поскольку вы не приписаны ни к одной группе!",
                    chatId
            );
            return;
        }

        Long groupId = user.getGroup().getId();

        // Получаем только актуальные мероприятия (с текущего момента и позже)
        List<Event> events = eventRepository
                .findAllByGroupIdAndDateAfterOrderByDateAsc(groupId, LocalDateTime.now());

        if (events.isEmpty()) {
            messageSender.sendAnswer("🎉 Ближайших мероприятий нет.", chatId);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📅 Ближайшие мероприятия:\n\n");

        for (Event event : events) {
            sb.append("🔹 ")
              .append(event.getName())
              .append("\n");

            if (event.getDescription() != null && !event.getDescription().isBlank()) {
                sb.append("📝 ")
                  .append(event.getDescription())
                  .append("\n");
            }

            sb.append("🕒 ")
              .append(event.getDate().format(FORMATTER))
              .append("\n\n");
        }

        messageSender.sendAnswer(sb.toString(), chatId);
    }
}