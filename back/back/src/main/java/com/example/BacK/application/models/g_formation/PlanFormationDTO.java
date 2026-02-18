package com.example.BacK.application.models.g_formation;

import com.example.BacK.domain.g_Formation.enumEntity.StatutFormation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanFormationDTO {

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
}
