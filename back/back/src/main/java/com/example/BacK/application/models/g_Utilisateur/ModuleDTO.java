package com.example.BacK.application.models.g_Utilisateur;

import com.example.BacK.domain.g_Utilisateurs.Module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDTO {
    
    private String code;
    private String displayName;
    private int permissionCount;
    
    /**
     * Constructeur à partir d'un enum Module
     */
    public ModuleDTO(Module module) {
        this.code = module.name();
        this.displayName = module.getDisplayName();
        this.permissionCount = 0;
    }
}
