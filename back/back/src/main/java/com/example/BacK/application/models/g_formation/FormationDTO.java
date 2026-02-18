package com.example.BacK.application.models.g_formation;

import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.domain.g_Formation.Domaine;
import com.example.BacK.domain.g_Formation.SousCategorie;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.domain.g_Formation.enumEntity.NiveauFormation;
import com.example.BacK.domain.g_Formation.enumEntity.TypeFormation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormationDTO {
    private Long idFormation;
    private String theme;
    private String descriptionTheme;
    private String objectifGlobal;
    private Integer nombreHeures;
    private Double prix;
    private Integer nombreMax;
    private String populationCible;
    private TypeFormation typeFormation;
    private NiveauFormation niveau;
    private DomaineDTO domaine;
    private TypeDTO type;
    private CategorieDTO categorie;
    private SousCategorieDTO sousCategorie;
}




