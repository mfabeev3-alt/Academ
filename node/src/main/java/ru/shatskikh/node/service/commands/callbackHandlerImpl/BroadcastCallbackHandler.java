package ru.shatskikh.node.service.commands.callbackHandlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Faculty;
import ru.shatskikh.entity.enums.Course;
import ru.shatskikh.entity.enums.UserState;
import ru.shatskikh.node.service.commands.CallbackHandler;
import ru.shatskikh.node.utils.MessageSender;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BroadcastCallbackHandler implements CallbackHandler {

    private final AppUserRepository appUserRepository;
    private final MessageSender messageSender;
    private final FacultyRepository facultyRepository;

    @Override
    public boolean isSupported(String data) {
        return data.startsWith("bc_");
    }

    @Override
    public void handle(Update update, AppUser user) {

        var query = update.getCallbackQuery();
        var data = query.getData();
        var chatId = query.getMessage().getChatId();
        var messageId = query.getMessage().getMessageId();

        if(data.equals("bc_faculty")){
            user.setUserState(UserState.AWAITING_BROADCAST_CONTENT);
            user.setTempData("FACULTY_SELECT");
            appUserRepository.save(user);

            sendFacultyList(chatId, messageId);
        } else if (data.startsWith("bc_fac_")) {
            String facultyId = data.replace("bc_fac_","");
            
            user.setTempData("COURSE_SELECT_" + facultyId);
            appUserRepository.save(user);
            
            sendCourseList(chatId,messageId, facultyId);
        } else if (data.startsWith("bc_course_")) {

            String [] parts = data.replace("bc_course_","").split("_");
            String facultyId = parts[0];
            String courseValue = parts[1];

            user.setTempData("READY_" + facultyId + "_" + courseValue);
            appUserRepository.save(user);

            messageSender.sendEditAnswer(chatId, messageId, "✅ Отлично! Теперь отправьте текст рассылки.", null);

        } else if(data.startsWith("bc_all")) {

            user.setTempData("READY_ALL");
            appUserRepository.save(user);

            messageSender.sendEditAnswer(chatId, messageId, "✅ Отлично! Теперь отправьте текст рассылки.", null);

        }

    }

    private void sendCourseList(Long chatId, Integer messageId, String facultyId) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for(Course course: Course.values()){

            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(course.getDescription())
                            .callbackData("bc_course_" + course.getValue() + "_" + facultyId)
                            .build()
            ));

        }

        markup.setKeyboard(rows);
        messageSender.sendEditAnswer(chatId, messageId, "\uD83D\uDCA1 Выберите курс.", markup);

    }

    private void sendFacultyList(Long chatId, Integer messageId) {

      List<Faculty> faculties = facultyRepository.findAll();
        
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for(Faculty faculty: faculties){
            
            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(faculty.getName())
                            .callbackData("bc_fac_" + faculty.getId())
                            .build()
            ));
            
        }

        markup.setKeyboard(rows);
        messageSender.sendEditAnswer(chatId, messageId, "\uD83C\uDFDA Выберите факультет.", markup);
    }

}
