package com.example.BacK.application.g_Utilisateur.Command.user.assignRole;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignRoleResponse {
    private String message;
    
    public AssignRoleResponse() {
        this.message = "Rôle assigné avec succès";
    }
}
