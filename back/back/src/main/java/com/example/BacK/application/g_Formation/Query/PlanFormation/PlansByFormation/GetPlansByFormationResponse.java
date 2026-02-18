package com.example.BacK.application.g_Formation.Query.PlanFormation.PlansByFormation;

import com.example.BacK.application.models.g_formation.FormateurDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPlansByFormationResponse {

    private Long idPlanFormation;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private Date dateLancement;
    private Date dateFinReel;
    private String statusFormation;
    private Long idFormation;
    private FormateurDTO formateur;
    private Integer nombreJours;


}
