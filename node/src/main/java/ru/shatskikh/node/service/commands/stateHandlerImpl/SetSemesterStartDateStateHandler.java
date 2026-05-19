package ru.shatskikh.node.service.commands.stateHandlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Semester;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.SemesterRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetSemesterStartDateStateHandler implements StateHandler {

    private final SemesterRepository semesterRepository;
    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    @Transactional
    public void handle(Update update, AppUser admin) {

        Long chatId = admin.getTelegramUserId();

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            messageSender.sendAnswer(
                    "❌ Введите дату в формате ДД.ММ.ГГГГ",
                    chatId
            );
            return;
        }

        String text = update.getMessage().getText().trim();
        LocalDate startDate;

        try {
            startDate = LocalDate.parse(text, FORMATTER);
        } catch (DateTimeParseException e) {
            messageSender.sendAnswer(
                    "❌ Неверный формат даты.\n\nПример: 01.09.2026",
                    chatId
            );
            return;
        }

        // Деактивируем текущий активный семестр
        semesterRepository.findByIsActiveTrue()
                .ifPresent(semester -> semester.setIsActive(false));

        // Создаем новый активный семестр
        Semester semester = Semester.builder()
                .startDate(startDate)
                .isActive(true)
                .description("Семестр от " + startDate.format(FORMATTER))
                .build();

        semesterRepository.save(semester);

        log.info("Модератор {} установил дату начала семестра: {}",
                admin.getUsername(),
                startDate);

        messageSender.sendAnswer(
                "✅ Дата начала семестра успешно установлена: "
                        + startDate.format(FORMATTER),
                chatId
        );

        // Возвращаем пользователя в обычное состояние
        admin.setUserState(UserState.IDLE);
        appUserRepository.save(admin);
    }

    @Override
    public UserState getSupportedState() {
        return UserState.AWAITING_SEMESTER_START_DATE;
    }
}