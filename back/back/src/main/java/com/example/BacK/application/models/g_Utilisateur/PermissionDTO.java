package com.example.BacK.application.models.g_Utilisateur;

import com.example.BacK.domain.g_Utilisateurs.Module;
import com.example.BacK.domain.g_Utilisateurs.PermissionAction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionDTO {
    private String id;
    @JsonProperty("resource")
    private String ressource;
    private PermissionAction action;
    private String description;
    @JsonProperty("displayName")
    private String nomAffichage;
    private Module module;

    public String getPermissionString() {
        return ressource + "_" + action.name();
    }
}
