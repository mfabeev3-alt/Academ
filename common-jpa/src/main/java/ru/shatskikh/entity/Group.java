package ru.shatskikh.entity;


import jakarta.persistence.*;
import lombok.*;
import ru.shatskikh.entity.Schedule.Professor;
import ru.shatskikh.entity.enums.Course;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "app_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Course course;

    @OneToMany(mappedBy = "group")
    private List<AppUser> students;

    @OneToMany(mappedBy = "group")
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    Faculty faculty;



}
