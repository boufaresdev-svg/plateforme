package com.example.BacK.application.g_Utilisateur.Command.role.removePermission;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemovePermissionCommand {
    
    @NotBlank(message = "L'ID du rôle est obligatoire")
    private String roleId;
    
    @NotBlank(message = "L'ID de la permission est obligatoire")
    private String permissionId;
}
