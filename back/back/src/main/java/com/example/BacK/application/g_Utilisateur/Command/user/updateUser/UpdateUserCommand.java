package com.example.BacK.application.g_Utilisateur.Command.user.updateUser;

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
public class UpdateUserCommand {
    
    @NotBlank(message = "L'ID de l'utilisateur est obligatoire")
    private String id;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
    
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse; // Optional - only update if provided
    
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
