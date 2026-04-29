package ru.shatskikh.node.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shatskikh.node.entity.RawData;

@Repository
public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
