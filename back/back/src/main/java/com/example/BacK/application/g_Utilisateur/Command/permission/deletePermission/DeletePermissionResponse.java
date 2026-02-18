package com.example.BacK.application.g_Utilisateur.Command.permission.deletePermission;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeletePermissionResponse {
    private String message;
    
    public DeletePermissionResponse() {
        this.message = "Permission supprimée avec succès";
    }
}
