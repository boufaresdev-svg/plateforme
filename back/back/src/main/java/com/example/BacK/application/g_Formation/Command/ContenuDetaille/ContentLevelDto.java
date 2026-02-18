package com.example.BacK.application.g_Formation.Command.ContenuDetaille;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for ContentLevel to avoid Jackson serialization issues with bidirectional relationships
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentLevelDto {

    private Integer levelNumber; // 1=Débutant, 2=Intermédiaire, 3=Avancé
    
    private String theorieContent; // Rich text content for this level
    
    private String pratiqueContent; // Practical content for this level
    
    private Double dureeTheorique; // Duration in hours
    
    private Double dureePratique; // Duration in hours
    
    private List<String> formationLevels; // Formation levels this applies to (Débutant, Intermédiaire, Avancé)

    // Note: Files are not included in DTO as they are uploaded separately
}
