package com.example.BacK.application.g_Utilisateur.Command.role.addRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoleCommand {
    
    @NotBlank(message = "Le nom du rôle est obligatoire")
    private String nom;
    
    private String description;
    
    private Set<String> permissionIds;
}
