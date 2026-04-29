package ru.shatskikh.node.entity;


import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_data")
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Update event;

}
