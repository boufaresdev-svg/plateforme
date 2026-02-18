package com.example.BacK.application.g_Formation.Command.ContenuDetaille.addContenuDetaille;

import com.example.BacK.application.g_Formation.Command.ContenuDetaille.ContentLevelDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Command to create a new ContenuDetaille with multi-level content support
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddContenuDetailleCommand {

    private String titre; // Content title
    
    private List<String> contenusCles; // Key content points
    
    private String methodesPedagogiques; // Teaching methods
    
    private Double dureeTheorique; // Theory duration in hours
    
    private Double dureePratique; // Practice duration in hours
    
    private String tags; // Search tags separated by semicolons
    
    /**
     * Multi-level content (1-3 levels)
     * Each level has theory content and duration
     */
    private List<ContentLevelDto> levels;
    
    // Optional: Can be assigned later via drag-and-drop
    private Long idJourFormation;
}
