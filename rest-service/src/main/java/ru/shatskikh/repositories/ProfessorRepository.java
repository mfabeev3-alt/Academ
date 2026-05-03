package ru.shatskikh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.Schedule.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
