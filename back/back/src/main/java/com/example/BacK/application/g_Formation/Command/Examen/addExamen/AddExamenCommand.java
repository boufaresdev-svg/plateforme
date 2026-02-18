package com.example.BacK.application.g_Formation.Command.Examen.addExamen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddExamenCommand {

    private String type;
    private Date date;
    private String description;
    private Double score;
    private Long idApprenant;
    private Long idPlanFormation;
}
