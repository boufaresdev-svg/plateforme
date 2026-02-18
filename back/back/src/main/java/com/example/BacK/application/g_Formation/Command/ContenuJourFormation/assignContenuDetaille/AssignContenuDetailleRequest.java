package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.assignContenuDetaille;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignContenuDetailleRequest {
    private List<Long> idContenusDetailles;
    private Integer niveau;
    private String niveauLabel;
}
