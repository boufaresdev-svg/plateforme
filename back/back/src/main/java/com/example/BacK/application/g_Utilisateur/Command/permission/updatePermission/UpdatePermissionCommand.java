package com.example.BacK.application.g_Utilisateur.Command.permission.updatePermission;

import com.example.BacK.domain.g_Utilisateurs.Module;
import com.example.BacK.domain.g_Utilisateurs.PermissionAction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePermissionCommand {
    
    @NotBlank(message = "L'ID de la permission est obligatoire")
    private String id;
    
    @NotBlank(message = "La ressource de la permission est obligatoire")
    private String ressource;
    
    @NotNull(message = "L'action de la permission est obligatoire")
    private PermissionAction action;
    
    private String description;
    
    private String nomAffichage;
    
    @NotNull(message = "Le module de la permission est obligatoire")
    private Module module;
}
