package ru.shatskikh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.Schedule.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
