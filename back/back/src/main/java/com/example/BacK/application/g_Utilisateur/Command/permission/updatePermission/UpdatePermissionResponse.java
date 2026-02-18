package com.example.BacK.application.g_Utilisateur.Command.permission.updatePermission;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePermissionResponse {
    private String id;
    private String message;
    
    public UpdatePermissionResponse(String id) {
        this.id = id;
        this.message = "Permission mise à jour avec succès";
    }
}
