package com.example.BacK.application.g_Utilisateur.Command.role.deleteRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRoleCommand {
    
    @NotBlank(message = "L'ID du rôle est obligatoire")
    private String id;
}
