package com.example.BacK.application.g_Utilisateur.Command.role.removePermission;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemovePermissionResponse {
    private String message;
    
    public RemovePermissionResponse() {
        this.message = "Permission retirée avec succès";
    }
}
