package com.example.BacK.application.g_Formation.Query.ContenuJourFormation.ContenusByObjectif;


import com.example.BacK.application.models.g_formation.ObjectifSpecifiqueDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetContenusByObjectifResponse {

    private Long idContenuJour;
    private String contenu;
    private String moyenPedagogique;
    private String supportPedagogique;
    private Integer nbHeuresTheoriques;
    private Integer nbHeuresPratiques;
    private ObjectifSpecifiqueDTO objectifSpecifique;
    private Long idPlanFormation;
    private Integer numeroJour;
    private String staff;
    private String niveau;
}
