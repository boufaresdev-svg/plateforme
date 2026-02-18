package com.example.BacK.application.g_Utilisateur.Command.user.addUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserResponse {
    private String id;
    private String message;
    
    public AddUserResponse(String id) {
        this.id = id;
        this.message = "Utilisateur créé avec succès";
    }
}
