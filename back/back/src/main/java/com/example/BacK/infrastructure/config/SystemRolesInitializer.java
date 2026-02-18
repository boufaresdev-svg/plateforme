package com.example.BacK.infrastructure.config;

import com.example.BacK.domain.g_Utilisateurs.Permission;
import com.example.BacK.domain.g_Utilisateurs.Role;
import com.example.BacK.infrastructure.repository.g_Utilisateur.PermissionRepository;
import com.example.BacK.infrastructure.repository.g_Utilisateur.RoleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Initialise les rôles système (ADMIN et SUPER_ADMIN) avec toutes les permissions.
 * 
 * Caractéristiques:
 * - SUPER_ADMIN: Possède toutes les permissions du système (non modifiable via UI)
 * - ADMIN: Possède toutes les permissions par défaut (modifiable via UI)
 * - S'exécute après PermissionDataInitializer pour garantir que toutes les permissions existent
 * - Met à jour automatiquement les permissions si de nouveaux modules sont ajoutés
 */
@Component
@Order(2) // S'exécute après PermissionDataInitializer (Order 1)
public class SystemRolesInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SystemRolesInitializer.class);
    
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final String ADMIN_ROLE = "ADMIN";
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public SystemRolesInitializer(RoleRepository roleRepository, 
                                  PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("🔐 Initialisation des rôles système...");
        
        try {
            // Récupérer toutes les permissions du système
            List<Permission> allPermissions = permissionRepository.findAll();
            Set<Permission> permissionSet = new HashSet<>(allPermissions);
            
            logger.info("📋 {} permissions trouvées dans le système", allPermissions.size());
            
            // Créer ou mettre à jour SUPER_ADMIN
            createOrUpdateSuperAdmin(permissionSet);
            
            // Créer ou mettre à jour ADMIN
            createOrUpdateAdmin(permissionSet);
            
            logger.info("✅ Rôles système initialisés avec succès");
            
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation des rôles système", e);
        }
    }

    /**
     * Crée ou met à jour le rôle SUPER_ADMIN avec toutes les permissions.
     * Ce rôle ne peut pas être modifié via l'interface utilisateur.
     */
    private void createOrUpdateSuperAdmin(Set<Permission> allPermissions) {
        Role superAdmin = roleRepository.findByNom(SUPER_ADMIN_ROLE)
                .orElse(null);
        
        if (superAdmin == null) {
            // Créer le rôle SUPER_ADMIN
            superAdmin = new Role();
            superAdmin.setNom(SUPER_ADMIN_ROLE);
            superAdmin.setDescription(
                "Super Administrateur - Accès complet et total au système. " +
                "Ce rôle possède toutes les permissions et ne peut pas être modifié."
            );
            superAdmin.setPermissions(new HashSet<>(allPermissions));
            superAdmin.setSystemRole(true); // Marquer comme rôle système
            roleRepository.save(superAdmin);
            logger.info("👑 Rôle SUPER_ADMIN créé avec {} permissions", allPermissions.size());
        } else {
            // Mettre à jour les permissions (toujours toutes les permissions)
            superAdmin.setPermissions(new HashSet<>(allPermissions));
            superAdmin.setSystemRole(true); // S'assurer qu'il est marqué comme système
            roleRepository.save(superAdmin);
            logger.info("🔄 Rôle SUPER_ADMIN mis à jour avec {} permissions", allPermissions.size());
        }
    }

    /**
     * Crée ou met à jour le rôle ADMIN avec toutes les permissions.
     * Ce rôle peut être modifié via l'interface utilisateur.
     */
    private void createOrUpdateAdmin(Set<Permission> allPermissions) {
        Role admin = roleRepository.findByNom(ADMIN_ROLE)
                .orElse(null);
        
        if (admin == null) {
            // Créer le rôle ADMIN
            admin = new Role();
            admin.setNom(ADMIN_ROLE);
            admin.setDescription(
                "Administrateur - Accès complet au système par défaut. " +
                "Les permissions peuvent être modifiées selon les besoins."
            );
            admin.setPermissions(new HashSet<>(allPermissions));
            admin.setSystemRole(false); // Peut être modifié
            roleRepository.save(admin);
            logger.info("👨‍💼 Rôle ADMIN créé avec {} permissions", allPermissions.size());
        } else {
            // Pour ADMIN, on ajoute seulement les nouvelles permissions
            // sans supprimer celles qui ont été révoquées manuellement
            Set<Permission> currentPermissions = admin.getPermissions();
            int initialSize = currentPermissions.size();
            
            // Ajouter les nouvelles permissions qui n'existent pas encore
            allPermissions.forEach(permission -> {
                if (!currentPermissions.contains(permission)) {
                    currentPermissions.add(permission);
                }
            });
            
            int addedCount = currentPermissions.size() - initialSize;
            if (addedCount > 0) {
                admin.setPermissions(currentPermissions);
                roleRepository.save(admin);
                logger.info("🔄 Rôle ADMIN mis à jour: {} nouvelles permissions ajoutées", addedCount);
            } else {
                logger.info("ℹ️ Rôle ADMIN: aucune nouvelle permission à ajouter");
            }
        }
    }
}
