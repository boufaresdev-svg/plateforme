package com.example.BacK.application.g_Formation.Query.PlanFormation;

import com.example.BacK.domain.g_Formation.enumEntity.StatutFormation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPlanFormationResponse {

    private Long idPlanFormation;
    private String titre;
    private String description;
    private Date dateLancement;
    private Date dateDebut;
    private Date dateFin;
    private Date dateFinReel;
    private StatutFormation statusFormation;
    private Long formationId;
    private Long formateurId;
    private Integer nombreJours;

}
