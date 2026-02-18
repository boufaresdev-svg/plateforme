package com.example.BacK.application.models.g_projet;

import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_Projet.enumEntity.PhaseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhaseDTO {

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
    private List<String> livrables;
}
