package ru.shatskikh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.shatskikh.entity.AppUser;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.enums.UserRole;

import java.util.List;

@Repository
@Component
public interface AppUserRepository extends JpaRepository <AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
    List<AppUser> findAppUserByUserRoleAndGroup(UserRole userRole, Group group);
}
