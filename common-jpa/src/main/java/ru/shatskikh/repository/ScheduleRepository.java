package ru.shatskikh.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.shatskikh.entity.Schedule.Schedule;
import ru.shatskikh.entity.enums.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByGroupId(Long groupId);
    List<Schedule> findByGroupIdAndDayOfWeek(Long groupId, DayOfWeek dayOfWeek);

    @Query ("DELETE FROM Schedule s WHERE s.group.id =:groupId")
    @Modifying
    @Transactional
    void deleteByGroupId(@Param("groupId") Long groupId);

    @Query ("DELETE FROM Schedule s WHERE s.movedDate = :today")
    @Modifying
    @Transactional
    void deleteExpiredMovedItems(@Param("today") LocalDate today);

    // Метод для поиска записей в расписании на определенную неделю и день
    @Query  ("SELECT s FROM Schedule s JOIN s.activeWeeks w " +

            "WHERE s.group.id = :groupId " +

            "AND s.dayOfWeek = :dayOfWeek " +

            "AND w = :weekNumber " +

            "ORDER BY s.startTime ASC")

    List<Schedule> findScheduleForDayAndWeek(
            @Param("groupId") Long groupId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("weekNumber") Integer weekNumber);


    // Метод для поиска записей в расписания на определенную неделю
    @Query  ("SELECT s FROM Schedule s JOIN s.activeWeeks w " +
            "WHERE s.group.id = :groupId " +
            "AND w = :weekNumber " +
            "ORDER BY s.dayOfWeek, s.startTime ASC")

    List<Schedule> findScheduleForWeek(
            @Param("groupId") Long groupId,
            @Param("weekNumber") Integer weekNumber);

    //Метод для поиска текущих пар
    @Query ("SELECT s FROM Schedule s " +
            "JOIN s.activeWeeks w " +
            "JOIN FETCH s.subject " +
            "JOIN FETCH s.professor " +
            "WHERE s.group.id =:groupId " +
            "AND s.dayOfWeek =:day " +
            "AND w =:week " +
            "AND s.endTime > :currentTime " +
            "ORDER BY s.startTime ASC")

    List<Schedule> findCurrentAndRemainingItems(
            @Param("groupId") Long groupId,
            @Param("day") DayOfWeek day,
            @Param("currentTime") LocalTime currentTime,
            @Param("week") Integer week
            );

    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.subject " +
            "JOIN FETCH s.professor " +
            "JOIN s.activeWeeks w " +
            "WHERE s.group.id = :groupId " +
            "AND s.dayOfWeek = :day " +
            "AND w = :week " +
            "ORDER BY s.startTime ASC")

    List<Schedule> findFirstLessonByDayAndWeek(
            @Param("groupId") Long groupId,
            @Param("day") DayOfWeek day,
            @Param("week") Integer week
    );


    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.subject " +
            "JOIN FETCH s.professor " +
            "JOIN s.activeWeeks w " +
            "WHERE s.group.id = :groupId " +
            "AND w = :weekNum " +
            "ORDER BY s.dayOfWeek ASC, s.startTime ASC")
    List<Schedule> findAllByGroupIdAndWeekNum(
            @Param("groupId") Long groupId,
            @Param("weekNum") Integer weekNum
    );


}
