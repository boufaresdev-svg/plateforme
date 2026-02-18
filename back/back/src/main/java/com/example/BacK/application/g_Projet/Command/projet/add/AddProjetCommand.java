package com.example.BacK.application.g_Projet.Command.projet.add;

import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutProjet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProjetCommand {

    private String nom;
    private String description;
    private String type;
    private StatutProjet statut;
    private PrioriteProjet priorite;
    private String chefProjet;
    private String client;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private Double budget;
    private Double coutReel;


}
