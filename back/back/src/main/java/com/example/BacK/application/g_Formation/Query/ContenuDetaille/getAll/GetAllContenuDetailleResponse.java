package com.example.BacK.application.g_Formation.Query.ContenuDetaille.getAll;

import com.example.BacK.domain.g_Formation.ContentLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllContenuDetailleResponse {

    private Long idContenuDetaille;
    private String titre;
    private List<String> contenusCles;
    private String methodesPedagogiques;
    private Double dureeTheorique;
    private Double dureePratique;
    private String tags;
    private Set<ContentLevel> levels;
    private Long idJourFormation; // null if not assigned yet
}
