package com.example.BacK.application.g_Utilisateur.Command.permission.addPermission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPermissionResponse {
    private String id;
    private String message;
    
    public AddPermissionResponse(String id) {
        this.id = id;
        this.message = "Permission créée avec succès";
    }
}
