package ru.shatskikh.node.service.commands.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.repository.EventRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventCleanupService {

    private final EventRepository eventRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredEvents() {

        int deletedCount = eventRepository.deleteByDateBefore(LocalDateTime.now());

        if (deletedCount > 0) {
            log.info("Удалено {} устаревших мероприятий.", deletedCount);
        } else {
            log.info("Устаревших мероприятий не найдено.");
        }
    }
}