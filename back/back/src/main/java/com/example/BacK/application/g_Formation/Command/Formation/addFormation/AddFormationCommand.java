package com.example.BacK.application.g_Formation.Command.Formation.addFormation;


import com.example.BacK.domain.g_Formation.enumEntity.NiveauFormation;
import com.example.BacK.domain.g_Formation.enumEntity.TypeFormation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFormationCommand {

    private String theme;
    private String descriptionTheme;
    private List<Long> objectifsGlobauxIds;
    private String objectifSpecifiqueGlobal;
    private String tags;
    private Integer nombreHeures;
    private Double prix;
    private Integer nombreMax;
    private String populationCible;
    private String imageUrl;
    private TypeFormation typeFormation;
    private NiveauFormation niveau;
    private String statut; // Brouillon or Publié
    private Long idDomaine;
    private Long idType;
    private Long idCategorie;
    private Long idSousCategorie;
    
    // Programme Détaillé
    private List<ProgrammeDetaileDTO> programmesDetailes;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgrammeDetaileDTO {
        private String titre;
        private List<JourFormationDTO> jours;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JourFormationDTO {
        private Integer numeroJour;
        private List<ContenuDetailleDTO> contenus;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContenuDetailleDTO {
        private String titre;
        private List<String> contenusCles;
        private String methodesPedagogiques;
        private Double dureeTheorique; // Overall duration (optional, can be calculated from levels)
        private Double dureePratique;  // Overall duration (optional, can be calculated from levels)
        private List<ContentLevelDTO> levels; // Multi-level content (1-5)
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentLevelDTO {
        private Integer levelNumber; // 1-5
        private String theorieContent; // Rich text content
        private String pratiqueContent; // Practical content (optional)
        private Double dureeTheorique; // Duration for this level's theory
        private Double dureePratique; // Duration for this level's practice
    }
}

