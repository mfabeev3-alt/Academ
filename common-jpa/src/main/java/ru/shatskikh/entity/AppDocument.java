package ru.shatskikh.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.shatskikh.entity.enums.FileType;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_document")
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFileId;
    private String docName;
    private String mimeType;
    private Long fileSize;
    private String s3Key;
    @Enumerated(EnumType.STRING)
    private FileType fileType;

}
