package com.example.BacK.application.g_Utilisateur.Command.role.updateRole;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRoleResponse {
    private String id;
    private String message;
    
    public UpdateRoleResponse(String id) {
        this.id = id;
        this.message = "Rôle mis à jour avec succès";
    }
}
