package com.example.BacK.application.g_Formation.Query.ContenuJourFormation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetContenuJourFormationResponse {

    private Long idContenuJour;
    private String contenu;
    private String moyenPedagogique;
    private String supportPedagogique;
    private int nbHeuresTheoriques;
    private int nbHeuresPratiques;
    private Long objectifSpecifiqueId;
    private Long idPlanFormation;
    private List<Long> assignedContenuDetailleIds;
    private Integer numeroJour;
    private String staff;
    private String niveau;
}
