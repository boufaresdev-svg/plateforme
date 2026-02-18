package com.example.BacK.application.g_Formation.Command.Examen.updateExamen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExamenCommand {

    private Long idExamen;
    private String type;
    private Date date;
    private String description;
    private Double score;
    private Long idApprenant;
    private Long idPlanFormation;
}
