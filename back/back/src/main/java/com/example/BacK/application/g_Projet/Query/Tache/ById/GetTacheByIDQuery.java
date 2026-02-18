package com.example.BacK.application.g_Projet.Query.Tache.ById;

import com.example.BacK.application.models.g_projet.ChargeDTO;
import com.example.BacK.application.models.g_projet.CommentaireTacheDTO;
import com.example.BacK.application.models.g_projet.EmployeAffecteDTO;
import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteTache;
import com.example.BacK.domain.g_Projet.enumEntity.StatutTache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTacheByIDQuery {
    private String id;

}
