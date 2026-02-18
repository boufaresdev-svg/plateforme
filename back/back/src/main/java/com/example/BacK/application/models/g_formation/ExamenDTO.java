package com.example.BacK.application.models.g_formation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamenDTO {
    private Long idExamen;
    private String type;
    private Date date;
    private String description;
    private Double score;
    private Long idApprenant;
}
