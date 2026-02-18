package com.example.BacK.application.g_Utilisateur.Command.user.deleteUser;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteUserResponse {
    private String message;
    
    public DeleteUserResponse() {
        this.message = "Utilisateur supprimé avec succès";
    }
}
