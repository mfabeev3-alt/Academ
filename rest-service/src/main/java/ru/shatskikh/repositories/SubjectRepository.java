package ru.shatskikh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.Schedule.Subject;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findAllByGroupId(Long groupId);

}
