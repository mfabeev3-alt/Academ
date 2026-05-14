package ru.shatskikh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Faculty;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.enums.Course;
import ru.shatskikh.entity.enums.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
@Component
public interface AppUserRepository extends JpaRepository <AppUser, Long> {

    Optional<AppUser> findAppUserByTelegramUserId(Long id);

    List<AppUser> findAppUserByUserRoleAndGroup(UserRole userRole, Group group);
    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAllByGroup_EntryYearAndGroup_Faculty(int group_entryYear, Faculty group_faculty);


    List<AppUser> findByUserRoleIn(List<UserRole> roles);

}
