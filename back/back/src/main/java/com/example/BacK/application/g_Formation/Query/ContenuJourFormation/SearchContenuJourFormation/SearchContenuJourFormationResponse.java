package com.example.BacK.application.g_Formation.Query.ContenuJourFormation.SearchContenuJourFormation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchContenuJourFormationResponse {
    private Long idContenuJour;
    private String contenu;
    private String moyenPedagogique;
    private String supportPedagogique;
    private int nbHeuresTheoriques;
    private int nbHeuresPratiques;
    private String tags;
    private Long objectifSpecifiqueId;
    private String objectifSpecifiqueTitre;
    private Long planFormationId;
    private boolean isCopied; // true if linked to an objectif specifique (copied), false otherwise (transferred)
    private Integer numeroJour;
    private String staff;
    private String niveau;
}
