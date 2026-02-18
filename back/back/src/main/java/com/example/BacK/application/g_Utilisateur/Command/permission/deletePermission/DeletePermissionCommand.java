package com.example.BacK.application.g_Utilisateur.Command.permission.deletePermission;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletePermissionCommand {
    
    @NotBlank(message = "L'ID de la permission est obligatoire")
    private String id;
}
