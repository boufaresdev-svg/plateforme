package com.example.BacK.application.g_Formation.Query.Evaluation.EvaluationsByContenuJour;

import com.example.BacK.application.models.g_formation.ApprenantDTO;
import com.example.BacK.application.models.g_formation.ContenuJourFormationDTO;
import com.example.BacK.application.models.g_formation.PlanFormationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEvaluationsByContenuJourResponse {

    private Long idEvaluation;
    private String type;
    private Date date;
    private String description;
    private Double score;
    private PlanFormationDTO  planFormationDTO      ;
    private ContenuJourFormationDTO  contenuJourFormationDTO;
    private ApprenantDTO  apprenantDTO  ;
    private Long idApprenant;


}
