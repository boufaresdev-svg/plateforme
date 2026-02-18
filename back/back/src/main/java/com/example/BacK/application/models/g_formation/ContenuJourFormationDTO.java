package com.example.BacK.application.models.g_formation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenuJourFormationDTO {

    private Long idContenuJour;
    private String contenu;
    private String moyenPedagogique;
    private String supportPedagogique;
    private int nbHeuresTheoriques;
    private int nbHeuresPratiques;
    private Integer numeroJour;
    private String staff;
    private String niveau;
    private Long idObjectifSpec;
    private Long idPlanFormation;
    private List<Long> assignedContenuDetailleIds;
}
