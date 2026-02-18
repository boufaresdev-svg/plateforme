package com.example.BacK.application.g_Formation.Query.ContenuJourFormation.GetAssignedContenus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAssignedContenusResponse {
    private Long idContenuDetaille;
    private String titre;
    private List<String> contenusCles;
    private String methodesPedagogiques;
    private Double dureeTheorique;
    private Double dureePratique;
    private Integer niveau;
    private String niveauLabel;
}
