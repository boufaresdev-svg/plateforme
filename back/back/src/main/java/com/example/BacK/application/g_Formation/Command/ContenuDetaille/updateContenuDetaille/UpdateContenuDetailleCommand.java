package com.example.BacK.application.g_Formation.Command.ContenuDetaille.updateContenuDetaille;

import com.example.BacK.application.g_Formation.Command.ContenuDetaille.ContentLevelDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Command to update an existing ContenuDetaille
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateContenuDetailleCommand {

    private Long idContenuDetaille; // Required: ID to update
    
    private String titre;
    
    private List<String> contenusCles;
    
    private String methodesPedagogiques;
    
    private Double dureeTheorique;
    
    private Double dureePratique;
    
    private String tags;
    
    private List<ContentLevelDto> levels;
    
    private Long idJourFormation; // Optional: can change jour
}
