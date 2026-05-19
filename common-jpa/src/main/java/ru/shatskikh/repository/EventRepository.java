package ru.shatskikh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByGroupId(Long groupId);
    int deleteByDateBefore(LocalDateTime date);
    List<Event> findAllByGroupIdAndDateAfterOrderByDateAsc(
            Long groupId,
            LocalDateTime date
    );
}
