package com.example.BacK.application.g_Utilisateur.Command.user.deleteUser;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteUserCommand {
    
    @NotBlank(message = "L'ID de l'utilisateur est obligatoire")
    private String id;
}
