package com.example.BacK.application.g_Utilisateur.Query.user;

import com.example.BacK.domain.g_Utilisateurs.UserStatus;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserQuery {
    private String id;
    private String nomUtilisateur;
    private String email;
    private String prenom;
    private String nom;
    private String numeroTelephone;
    private String departement;
    private String poste;
    private UserStatus statut;
    private String search; // Global search parameter
    
    // Pagination parameters
    @Min(value = 0, message = "Le numéro de page doit être supérieur ou égal à 0")
    private Integer page = 0;
    
    @Min(value = 1, message = "La taille de page doit être supérieure ou égale à 1")
    private Integer size = 10;
    
    private String sortBy = "nomUtilisateur";
    private String sortDirection = "ASC";
}
