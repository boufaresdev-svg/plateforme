package com.example.BacK.application.g_Utilisateur.Command.user.removeRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveRoleCommand {
    
    @NotBlank(message = "L'ID de l'utilisateur est obligatoire")
    private String userId;
    
    @NotBlank(message = "L'ID du rôle est obligatoire")
    private String roleId;
}
