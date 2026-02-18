package com.example.BacK.application.g_Formation.Command.Evaluation.updateEvaluation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEvaluationCommand {

    private Long idEvaluation;
    private String type;
    private Date date;
    private String description;
    private Double score;
    private Long idPlanFormation;
    private Long idContenuJourFormation;
    private Long idApprenant;



}
