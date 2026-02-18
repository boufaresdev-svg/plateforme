package com.example.BacK.application.g_Formation.Query.Apprenant;

import com.example.BacK.domain.g_Formation.enumEntity.StatusInscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetApprenantResponse {

    private Long idApprenant;
    private String matricule;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private String prerequis;
    private Boolean isBlocked;
    private Boolean isStaff;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private StatusInscription statusInscription;
    private Long planFormationId;
    private String planFormationTitre;
}
