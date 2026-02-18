package com.example.BacK.application.g_Utilisateur.Command.role.updateRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleCommand {
    
    @NotBlank(message = "L'ID du rôle est obligatoire")
    private String id;
    
    @NotBlank(message = "Le nom du rôle est obligatoire")
    private String nom;
    
    private String description;
    
    private Set<String> permissionIds;
}
