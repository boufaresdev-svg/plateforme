package com.example.BacK.application.g_Formation.Command.Apprenant.addApprenant;


import com.example.BacK.domain.g_Formation.enumEntity.StatusInscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddApprenantCommand {

    private String matricule;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private String prerequis;
    private String password;
    private Boolean isBlocked;
    private Boolean isStaff;
    private Boolean isActive;
    private StatusInscription statusInscription;
    private Long idPlanFormation;
}
