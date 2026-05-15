package ru.shatskikh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuUpdateDto implements Serializable {
    private Long userId;
    private String url;
    private String text;
}