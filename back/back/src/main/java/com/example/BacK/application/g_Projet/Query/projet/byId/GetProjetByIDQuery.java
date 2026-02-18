package com.example.BacK.application.g_Projet.Query.projet.byId;

import com.example.BacK.application.models.g_Client.ClientDTO;
import com.example.BacK.application.models.g_projet.MissionDTO;
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
public class GetProjetByIDQuery {
    private String id;

}
