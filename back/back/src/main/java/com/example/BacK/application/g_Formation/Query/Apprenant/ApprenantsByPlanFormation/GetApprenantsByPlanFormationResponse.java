package com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantsByPlanFormation;


import com.example.BacK.domain.g_Formation.enumEntity.StatusInscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetApprenantsByPlanFormationResponse {

    private Long idApprenant;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String prerequis ;
    private String adresse;
    private StatusInscription statusInscription;
    private Long idPlanFormation;


}
