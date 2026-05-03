package ru.shatskikh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.Schedule.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
