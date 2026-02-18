package com.example.BacK.application.g_Formation.Query.ContenuDetaille;

import com.example.BacK.domain.g_Formation.LevelFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for ContentLevel in responses - includes files
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentLevelResponseDto {
    private Long id;
    private Integer levelNumber;
    private String theorieContent;
    private Double dureeTheorique;
    private Double dureePratique;
    private List<LevelFile> files = new ArrayList<>();
}
