package com.example.BacK.application.g_Utilisateur.Query.role;

import com.example.BacK.application.models.g_Utilisateur.PermissionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoleResponse {
    private String id;
    private String name;
    private String description;
    private boolean systemRole;
    private Set<PermissionDTO> permissions;
}
