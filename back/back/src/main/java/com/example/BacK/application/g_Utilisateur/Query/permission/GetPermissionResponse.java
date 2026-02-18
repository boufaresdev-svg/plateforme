package com.example.BacK.application.g_Utilisateur.Query.permission;

import com.example.BacK.domain.g_Utilisateurs.Module;
import com.example.BacK.domain.g_Utilisateurs.PermissionAction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPermissionResponse {
    private String id;
    private String resource;
    private PermissionAction action;
    private String description;
    private String displayName;
    private Module module;
}
