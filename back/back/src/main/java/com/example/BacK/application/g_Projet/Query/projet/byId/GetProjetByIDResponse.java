package com.example.BacK.application.g_Projet.Query.projet.byId;

import com.example.BacK.application.models.g_Client.ClientDTO;
import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.application.models.g_projet.PhaseDTO;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutProjet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProjetByIDResponse {
    private String id;
    private String nom;
    private String description;
    private String type;
    private String statut;
    private PrioriteProjet priorite;
    private String chefProjet;
    private ClientDTO client;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateFinPrevue;
    private Double budget;
    private Double coutReel;
    private Double progression;
    private List<MissionDTO> missions;
    private List<PhaseDTO> phases;
    private List<String> documents;

}
