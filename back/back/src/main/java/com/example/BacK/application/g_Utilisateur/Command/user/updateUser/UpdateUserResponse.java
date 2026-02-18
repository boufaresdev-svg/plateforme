package com.example.BacK.application.g_Utilisateur.Command.user.updateUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponse {
    private String id;
    private String message;
    
    public UpdateUserResponse(String id) {
        this.id = id;
        this.message = "Utilisateur mis à jour avec succès";
    }
}
