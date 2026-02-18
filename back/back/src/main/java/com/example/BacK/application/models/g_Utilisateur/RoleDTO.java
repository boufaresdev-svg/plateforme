package com.example.BacK.application.models.g_Utilisateur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private String id;
    private String nom;
    private String description;
    private Set<PermissionDTO> permissions;
    private List<String> permissionIds;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
