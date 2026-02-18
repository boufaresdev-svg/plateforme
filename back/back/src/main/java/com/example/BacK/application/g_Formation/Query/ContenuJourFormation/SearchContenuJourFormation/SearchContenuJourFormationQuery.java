package com.example.BacK.application.g_Formation.Query.ContenuJourFormation.SearchContenuJourFormation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchContenuJourFormationQuery {
    private String contenu;
    private Long idObjectifSpecifique;
    private Boolean isCopied; // true = copié (has objectifSpecifique), false = transféré, null = all
}
