package com.example.BacK.application.models.g_Utilisateur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.example.BacK.domain.g_Utilisateurs.UserStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String nomUtilisateur;
    private String email;
    private String motDePasse;
    private String prenom;
    private String nom;
    private String numeroTelephone;
    private String departement;
    private String poste;
    private UserStatus statut;
    private Set<RoleDTO> roles;
    private List<String> roleIds;  // For create/update operations
    private LocalDateTime derniereConnexion;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
