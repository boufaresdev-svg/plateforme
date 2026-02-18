package com.example.BacK.application.g_Utilisateur.Query.user;

import com.example.BacK.application.models.g_Utilisateur.RoleDTO;
import com.example.BacK.domain.g_Utilisateurs.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponse {
    private String id;
    private String nomUtilisateur;
    private String email;
    private String prenom;
    private String nom;
    private String numeroTelephone;
    private String departement;
    private String poste;
    private UserStatus statut;
    private Set<RoleDTO> roles;
    private LocalDateTime derniereConnexion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
