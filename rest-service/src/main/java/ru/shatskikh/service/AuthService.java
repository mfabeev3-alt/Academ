package ru.shatskikh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shatskikh.DTO.TelegramUser;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.exceptions.AccessDeniedException;
import ru.shatskikh.entity.exceptions.EntityNotFoundException;
import ru.shatskikh.repository.AppUserRepository;
import ru.shatskikh.security.JwtService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TelegramService telegramService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final AppUserRepository userRepository;

    public String authenticate(Map<String, String> data) {
        // 1. Проверка подлинности подписи Telegram
        // Теперь передаем Map напрямую в валидатор
        if (!telegramService.isDataValid(data)) {
            throw new RuntimeException("Ошибка авторизации: неверная подпись данных");
        }

        try {
            // 2. Достаем JSON пользователя из Map
            // Поле "user" в Map — это строка JSON
            String userJson = data.get("user");
            if (userJson == null) {
                throw new RuntimeException("Данные пользователя отсутствуют в запросе");
            }

            TelegramUser tgUser = objectMapper.readValue(userJson, TelegramUser.class);

            // 3. Работа с базой данных
            AppUser user = userRepository.findAppUserByTelegramUserId(tgUser.id())
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + tgUser.id() + " не найден!"));

            // Проверка прав (только староста)
            if (user.getUserRole() != UserRole.ROLE_LEADER) {
                throw new AccessDeniedException("Доступ запрещен: только староста может редактировать расписание!");
            }

            // Проверка наличия группы
            if (user.getGroup() == null) {
                throw new RuntimeException("Пользователь не привязан ни к одной группе!");
            }

            Long groupId = user.getGroup().getId();

            // 4. Генерация JWT токена
            // Передаем Telegram ID и ID группы в токен
            return jwtService.generateToken(tgUser.id(), groupId);

        } catch (EntityNotFoundException | AccessDeniedException e) {
            // Пробрасываем специфичные ошибки дальше
            throw e;
        } catch (Exception e) {
            // Логируем и оборачиваем технические ошибки (ошибки парсинга и т.д.)
            throw new RuntimeException("Ошибка при обработке данных пользователя: " + e.getMessage());
        }
    }
}