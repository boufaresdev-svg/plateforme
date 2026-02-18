package com.example.BacK.application.models.g_projet;

import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutProjet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjetDTO {

    private String id;
    private String nom;
    private String description;
    private String type;
    private StatutProjet statut;
    private PrioriteProjet priorite;
    private String chefProjet;
    private String client;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateFinPrevue;
    private Double budget;
    private Double coutReel;
    private Double progression;
    private List<MissionDTO> missions;
    private List<PhaseDTO> phases;
    private List<String> documents;
    private List<String> tags;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
