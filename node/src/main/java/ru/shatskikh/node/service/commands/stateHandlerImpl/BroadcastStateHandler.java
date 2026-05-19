package ru.shatskikh.node.service.commands.stateHandlerImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Faculty;
import ru.shatskikh.entity.enums.Course;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.exceptions.FacultyNotFoundException;
import ru.shatskikh.node.service.commands.StateHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BroadcastStateHandler implements StateHandler {


    private final AppUserRepository appUserRepository;
    private final FacultyRepository facultyRepository;
    private final MessageSender messageSender;

    @Override
    public UserState getSupportedState() {
        return UserState.AWAITING_BROADCAST_CONTENT;
    }


    @Override
    public void handle(Update update, AppUser admin) {

        var message = update.getMessage();
        var text = message.getText();
        var chatId = message.getChatId();
        var tempData = admin.getTempData();


        if(tempData == null || !tempData.startsWith("READY_")){
            messageSender.sendAnswer("❌ Ошибка в последовательности действий.", chatId);
            return;
        }

            String [] parts = tempData.split("_");

        if (parts.length > 3) {
            messageSender.sendAnswer("❌ Ошибка: повреждены временные данные", chatId);
            return;
        }

        try {

            if(parts.length == 3) {

                Course targetCourse = Course.fromValue(Integer.parseInt(parts[1]));
                Long facultyId = Long.parseLong(parts[2]);

                Faculty targetFaculty = facultyRepository.findById(facultyId).orElseThrow(FacultyNotFoundException::new);


                LocalDate now = LocalDate.now();
                int currentYear = now.getYear();
                int entryYear;

                if (now.getMonthValue() >= 9) {

                    entryYear = currentYear - (targetCourse.getValue() - 1);
                } else {

                    entryYear = currentYear - targetCourse.getValue();
                }

                List<AppUser> recipients = appUserRepository.findAllByGroup_EntryYearAndGroup_Faculty(
                        entryYear, targetFaculty);

                for (AppUser recipient : recipients) {


                    if (message.hasPhoto()) {

                        messageSender.sendAnswer(update, recipient.getTelegramUserId());

                    } else {

                        messageSender.sendAnswer(text, recipient.getTelegramUserId());
                    }

                }

                admin.setTempData(null);
                admin.setUserState(UserState.IDLE);
                appUserRepository.save(admin);

                messageSender.sendAnswer("\uD83C\uDF89 Рассылка успешно завершена!", chatId);

            } else {

                List<AppUser> recipients = appUserRepository.findAll();

                for (AppUser recipient : recipients) {

                    if (message.hasPhoto()) {

                        messageSender.sendAnswer(update, recipient.getTelegramUserId());

                    } else {
                        messageSender.sendAnswer(text, recipient.getTelegramUserId());
                    }

                }

                admin.setTempData(null);
                admin.setUserState(UserState.IDLE);
                appUserRepository.save(admin);

                messageSender.sendAnswer("\uD83C\uDF89 Рассылка успешно завершена!", chatId);


            }

        } catch (NumberFormatException e) {

            messageSender.sendAnswer("❌ Ошибка: Неверный формат числа во временных данных", chatId);
        } catch (FacultyNotFoundException e) {
            messageSender.sendAnswer("❌ Ошибка: Неверный факультет!", chatId);
        }

    }


}
