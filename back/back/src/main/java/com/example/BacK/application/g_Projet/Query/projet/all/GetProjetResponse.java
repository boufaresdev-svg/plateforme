package com.example.BacK.application.g_Projet.Query.projet.all;

import com.example.BacK.application.models.g_Client.ClientDTO;
import com.example.BacK.application.models.g_Client.FactureClientDTO;
import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.application.models.g_projet.PhaseDTO;
import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Client.FactureClient;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutProjet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProjetResponse {

    private String id;
    private String nom;
    private String description;
    private String type;
    private StatutProjet statut;
    private PrioriteProjet priorite;
    private String chefProjet;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateFinPrevue;
    private Double budget;
    private Double coutReel;
    private Double progression;
    private ClientDTO client;
    private List<MissionDTO> missions ;
    private List<PhaseDTO> phases ;
    private List<FactureClientDTO> factures ;
    private List<String> documents ;
}
