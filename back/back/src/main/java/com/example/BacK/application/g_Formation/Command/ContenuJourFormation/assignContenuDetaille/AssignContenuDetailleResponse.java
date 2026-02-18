package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.assignContenuDetaille;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignContenuDetailleResponse {
    private Long idContenuJour;
    private String message;
    private int nbContenusAssignes;
}
