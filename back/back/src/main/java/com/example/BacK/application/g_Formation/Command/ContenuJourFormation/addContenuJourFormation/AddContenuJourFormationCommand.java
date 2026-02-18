package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.addContenuJourFormation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddContenuJourFormationCommand {

    private String contenu;
    private String moyenPedagogique;
    private String supportPedagogique;
    private int nbHeuresTheoriques;
    private int nbHeuresPratiques;
    private Long idObjectifSpecifique;
    private Long idPlanFormation;
    private Integer numeroJour;
    private String staff;
    private String niveau;
}
