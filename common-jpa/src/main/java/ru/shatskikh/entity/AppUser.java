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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegramUserId;

    @CreationTimestamp
    private Instant firstLogin;

    private String firstName;
    private String lastName;
    private String username;
    private String fio;

    private String email;
    private Boolean isApproved;

    @Enumerated (EnumType.STRING)  //ordinal – возврращает объект типа Integer, т.е. порядковый номер объекта в Enum. String – возвращает название константы
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserState userState;

    @ManyToOne
    @JoinColumn(name = "id")
    private Group group;

}
