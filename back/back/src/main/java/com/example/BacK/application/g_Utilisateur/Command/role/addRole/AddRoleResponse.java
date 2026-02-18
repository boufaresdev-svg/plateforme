package com.example.BacK.application.g_Utilisateur.Command.role.addRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoleResponse {
    private String id;
    private String message;
    
    public AddRoleResponse(String id) {
        this.id = id;
        this.message = "Rôle créé avec succès";
    }
}
