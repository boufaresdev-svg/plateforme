package com.example.BacK.application.g_Utilisateur.Command.role.assignPermission;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignPermissionResponse {
    private String message;
    
    public AssignPermissionResponse() {
        this.message = "Permission assignée avec succès";
    }
}
