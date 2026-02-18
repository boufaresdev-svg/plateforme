package com.example.BacK.application.g_Formation.Query.formation.formation;

import com.example.BacK.domain.g_Formation.enumEntity.NiveauFormation;
import com.example.BacK.domain.g_Formation.enumEntity.TypeFormation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormationSummaryDTO {
    private Long idFormation;
    private String theme;
    private String descriptionTheme;
    private Integer nombreHeures;
    private Double prix;
    private Integer nombreMax;
    private String populationCible;
    private TypeFormation typeFormation;
    private NiveauFormation niveau;
    private String categorie;
    private String statut;
}
