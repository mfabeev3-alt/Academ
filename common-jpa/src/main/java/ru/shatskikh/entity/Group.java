package ru.shatskikh.entity;


import jakarta.persistence.*;
import lombok.*;
import ru.shatskikh.entity.Schedule.Professor;
import ru.shatskikh.entity.Schedule.Subject;
import ru.shatskikh.entity.enums.Course;

import java.time.LocalDate;
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

    @Column(name = "name")
    private String name;

    @Column(name = "entry_year", nullable = false)
    private int entryYear;

    @OneToMany(mappedBy = "group")
    private List<AppUser> students;

    @OneToMany(mappedBy = "group")
    private List<Professor> professors;

    @OneToMany(mappedBy = "group")
    private List<Subject> subjects;

    @OneToMany(mappedBy = "group")
    private List<Event> events;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    Faculty faculty;

    public Course getCourse() {

        LocalDate now = LocalDate.now();
        int course = now.getYear() - entryYear;

        if(now.getMonthValue() >= 9) {
            course++;
        }

        return Course.fromValue(course);
    }

}
