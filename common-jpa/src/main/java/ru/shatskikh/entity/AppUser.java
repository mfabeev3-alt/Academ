package ru.shatskikh.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.shatskikh.entity.enums.UserRole;
import ru.shatskikh.entity.enums.UserState;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="telegram_user_id")
    private Long telegramUserId;

    @CreationTimestamp
    private Instant firstLogin;

    private String firstName;
    private String lastName;
    private String username;
    private String fio;
    private Boolean isApproved;

    @Enumerated (EnumType.STRING)  //ordinal – возврращает объект типа Integer, т.е. порядковый номер объекта в Enum. String – возвращает название константы
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserState userState;

    private String tempData;

    @ManyToOne
    @JoinColumn(name = "group_id") //Имя колонки в таблице app_user, хранящая ссылку на таблицу с группами
    private Group group;

}
