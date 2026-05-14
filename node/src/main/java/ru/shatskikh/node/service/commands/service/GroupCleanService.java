package ru.shatskikh.node.service.commands.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.repository.GroupRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupCleanService {

    private final GroupRepository groupRepository;

    @Scheduled(cron = "0 0 0 1 8 *")
    @Transactional
    public void deleteGraduatedGroup() {

        int currentYear = LocalDate.now().getYear();
        int graduationLimit = 4;

        int cutoffYear = currentYear - graduationLimit;
        log.info("Очистка групп, поступивщих в {} году", currentYear);

        groupRepository.deleteByEntryYearLessThanEqual(currentYear);


    }



}
