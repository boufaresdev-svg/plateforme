package com.example.BacK.application.g_Formation.Query.Evaluation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetEvaluationResponse {

    private Long idEvaluation;
    private String type;
    private Date date;
    private String description;
    private Double score;
    private Long PlanFormationId;
    private Long ContenuJourFormationId;
    private Long ApprenantId;





}
