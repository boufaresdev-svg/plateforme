package com.example.BacK.application.g_Formation.Query.Examen;

import com.example.BacK.application.models.g_formation.ApprenantDTO;
import com.example.BacK.application.models.g_formation.PlanFormationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetExamenResponse {

    private Long idExamen;
    private String type;
    private Date date;
    private String description;
    private Double score;
    private ApprenantDTO  apprenant;
    private PlanFormationDTO planFormation;
}
