package com.example.BacK.application.g_Utilisateur.Command.role.deleteRole;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteRoleResponse {
    private String message;
    
    public DeleteRoleResponse() {
        this.message = "Rôle supprimé avec succès";
    }
}
