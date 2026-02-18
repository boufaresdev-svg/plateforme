package com.example.BacK.application.g_Utilisateur.Command.user.addUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import com.example.BacK.domain.g_Utilisateurs.UserStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserCommand {
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String nomUtilisateur;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;
    
    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String numeroTelephone;
    
    @Size(max = 100, message = "Le département ne doit pas dépasser 100 caractères")
    private String departement;
    
    @Size(max = 100, message = "Le poste ne doit pas dépasser 100 caractères")
    private String poste;
    
    private UserStatus statut;
    
    private Set<String> roleIds;
}
