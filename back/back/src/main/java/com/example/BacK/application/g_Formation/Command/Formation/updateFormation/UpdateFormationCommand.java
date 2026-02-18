package com.example.BacK.application.g_Formation.Command.Formation.updateFormation;


import com.example.BacK.application.g_Formation.Command.Formation.addFormation.AddFormationCommand;
import com.example.BacK.domain.g_Formation.enumEntity.NiveauFormation;
import com.example.BacK.domain.g_Formation.enumEntity.TypeFormation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateFormationCommand {

    private Long idFormation;
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
    private Long idDomaine;
    private Long idType;
    private Long idCategorie;
    private Long idSousCategorie;
    
    // Programme Détaillé
    private List<AddFormationCommand.ProgrammeDetaileDTO> programmesDetailes;

}


