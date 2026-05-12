package ru.shatskikh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.Schedule.Schedule;
import ru.shatskikh.entity.enums.DayOfWeek;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByGroupId(Long groupId);
    List<Schedule> findByGroupIdAndDayOfWeek(Long groupId, DayOfWeek dayOfWeek);

    @Query  ("SELECT s FROM Schedule s JOIN s.activeWeeks w " +

            "WHERE s.group.id = :groupId " +

            "AND s.dayOfWeek = :dayOfWeek " +

            "AND w = :weekNumber " +

            "ORDER BY s.startTime ASC")

    List<Schedule> findScheduleForDayAndWeek(
            @Param("groupId") Long groupId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("weekNumber") Integer weekNumber);

    @Query  ("SELECT s FROM Schedule s JOIN s.activeWeeks w " +

            "WHERE s.group.id = :groupId " +

            "AND w = :weekNumber " +

            "ORDER BY s.dayOfWeek, s.startTime ASC")

    List<Schedule> findScheduleForWeek(@Param("groupId") Long groupId, @Param("weekNumber") Integer weekNumber);

}
