package com.example.BacK.application.g_Utilisateur.Command.user.removeRole;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveRoleResponse {
    private String message;
    
    public RemoveRoleResponse() {
        this.message = "Rôle retiré avec succès";
    }
}
