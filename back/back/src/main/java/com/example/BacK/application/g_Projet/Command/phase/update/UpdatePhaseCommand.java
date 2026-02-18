package com.example.BacK.application.g_Projet.Command.phase.update;

import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.application.models.g_projet.ProjetDTO;
import com.example.BacK.domain.g_Projet.enumEntity.PhaseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePhaseCommand {
    private String id;
    private String nom;
    private String description;
    private Integer ordre;
    private String statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Double budget;
    private String projet;

}
