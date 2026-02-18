package com.example.BacK.application.g_Formation.Query.formation.formation;

 import com.example.BacK.application.models.g_formation.CategorieDTO;
 import com.example.BacK.application.models.g_formation.DomaineDTO;
 import com.example.BacK.application.models.g_formation.ObjectifGlobalDTO;
 import com.example.BacK.application.models.g_formation.ObjectifSpecifiqueDTO;
 import com.example.BacK.application.models.g_formation.SousCategorieDTO;
 import com.example.BacK.application.models.g_formation.TypeDTO;
 import com.example.BacK.domain.g_Formation.enumEntity.NiveauFormation;
 import com.example.BacK.domain.g_Formation.enumEntity.TypeFormation;
 import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFormationResponse {

    private Long idFormation;
    private String theme;
    private String descriptionTheme;
    private List<ObjectifGlobalDTO> objectifsGlobaux;
    private List<ObjectifSpecifiqueDTO> objectifsSpecifiques;
    private String objectifSpecifiqueGlobal;
    private String tags;
    private Integer nombreHeures;
    private Double prix;
    private Integer nombreMax;
    private String populationCible;
    private String imageUrl;
    private TypeFormation typeFormation;
    private NiveauFormation niveau;
    private DomaineDTO domaine;
    private TypeDTO type;
    private CategorieDTO categorie;
    private SousCategorieDTO sousCategorie;
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
        private Long idProgramme;
        private String titre;
        private List<JourFormationDTO> jours;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JourFormationDTO {
        private Long idJour;
        private Integer numeroJour;
        private List<ContenuDetailleDTO> contenus;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContenuDetailleDTO {
        private Long idContenuDetaille;
        private List<String> contenusCles;
        private String methodesPedagogiques;
        private Double dureeTheorique;
        private Double dureePratique;
    }

}




