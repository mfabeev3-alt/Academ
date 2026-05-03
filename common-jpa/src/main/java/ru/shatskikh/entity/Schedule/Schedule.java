package ru.shatskikh.entity.Schedule;


import jakarta.persistence.*;
import lombok.*;
import ru.shatskikh.entity.Group;
import ru.shatskikh.entity.enums.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedule_item")

public class Schedule {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_item_group_id"))
    private Group group;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_item_professor_id"))
    private Professor professor;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_item_subject_id"))
    private Subject subject;

    @Column(name = "room", nullable = false)
    private String room;

    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private  LocalTime endTime;

    @Column(name = "is_moved", nullable = false)
    private boolean isMoved = false;

    @Column(name = "moved_date")
    private LocalDate movedDate;



}
