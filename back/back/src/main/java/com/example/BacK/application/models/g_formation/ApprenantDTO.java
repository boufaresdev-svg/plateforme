package com.example.BacK.application.models.g_formation;

import com.example.BacK.domain.g_Formation.enumEntity.StatusInscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprenantDTO {

    private Long idApprenant;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private String prerequis;
    private StatusInscription statusInscription;
    private Long idPlanFormation;
    private String titrePlanFormation;
}
