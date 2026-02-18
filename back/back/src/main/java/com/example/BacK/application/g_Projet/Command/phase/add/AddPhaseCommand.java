package com.example.BacK.application.g_Projet.Command.phase.add;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPhaseCommand {

    private String nom;
    private String description;
    private Integer ordre;
    private String statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double progression;
    private Double budget;
    private String projet;

}
