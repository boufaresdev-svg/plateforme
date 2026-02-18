package com.example.BacK.infrastructure.repository.g_Utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.BacK.domain.g_Utilisateurs.Permission;
import com.example.BacK.domain.g_Utilisateurs.PermissionAction;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    
    Optional<Permission> findByRessourceAndAction(String ressource, PermissionAction action);
    
    Boolean existsByRessourceAndAction(String ressource, PermissionAction action);
}
