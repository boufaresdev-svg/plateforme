package com.example.BacK.application.g_Projet.Query.phase.all;

import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.application.models.g_projet.ProjetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPhaseResponse {
    private String id;
    private String nom;
    private String description;
    private Integer ordre;
    private String statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double progression;
    private Double budget;
    private ProjetDTO projet;
    private List<MissionDTO> missions;
}
