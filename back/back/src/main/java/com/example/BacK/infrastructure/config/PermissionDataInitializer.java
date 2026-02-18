package com.example.BacK.infrastructure.config;

import com.example.BacK.domain.g_Utilisateurs.Module;
import com.example.BacK.domain.g_Utilisateurs.Permission;
import com.example.BacK.domain.g_Utilisateurs.PermissionAction;
import com.example.BacK.infrastructure.repository.g_Utilisateur.PermissionRepository;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Dynamically initializes permissions for all JPA entities in the system.
 * This component scans for @Entity classes and automatically creates CRUD permissions.
 * 
 * Features:
 * - Scans all @Entity annotated classes dynamically
 * - Creates permissions only if they don't exist (idempotent)
 * - Supports all standard actions (CREER, LIRE, MODIFIER, SUPPRIMER, EXPORTER, IMPORTER)
 * - Adds special sensitive data permissions
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionDataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final EntityManager entityManager;

    // Standard CRUD actions for all entities
    private static final PermissionAction[] STANDARD_ACTIONS = {
        PermissionAction.CREER,
        PermissionAction.LIRE,
        PermissionAction.MODIFIER,
        PermissionAction.SUPPRIMER,
        PermissionAction.EXPORTER,
        PermissionAction.IMPORTER
    };

    // Action labels in French
    private static final Map<PermissionAction, String> ACTION_LABELS = Map.of(
        PermissionAction.CREER, "Créer",
        PermissionAction.LIRE, "Voir",
        PermissionAction.MODIFIER, "Modifier",
        PermissionAction.SUPPRIMER, "Supprimer",
        PermissionAction.EXPORTER, "Exporter",
        PermissionAction.IMPORTER, "Importer"
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // Scan and initialize permissions dynamically
            int newPermissions = initializePermissions();
        } catch (Exception e) {
            log.error("❌ Permission initialization failed: {}", e.getMessage());
            log.error("🔧 Run the SQL script: backend/scripts/update_permissions_module_constraint.sql");
        }
    }

    private int initializePermissions() {
        int newPermissionsCount = 0;
        
        // Get all entity classes dynamically
        Set<Class<?>> entityClasses = scanForEntityClasses();

        // Create permissions for each entity
        for (Class<?> entityClass : entityClasses) {
            String entityName = getEntityName(entityClass);
            
            for (PermissionAction action : STANDARD_ACTIONS) {
                if (createPermissionIfNotExists(entityName, action)) {
                    newPermissionsCount++;
                }
            }
        }

        // Add special business permissions
        if (createSpecialPermissionIfNotExists("DONNEES_METIER", PermissionAction.LISTER_SENSIBLE, 
                "Permet de lister et voir toutes les données métier sensibles de l'entreprise", 
                "Lister Données Métier")) {
            newPermissionsCount++;
        }
        
        if (createSpecialPermissionIfNotExists("DONNEES_FINANCIERES", PermissionAction.LISTER_SENSIBLE, 
                "Permet de lister et voir toutes les données financières de l'entreprise", 
                "Lister Données Financières")) {
            newPermissionsCount++;
        }
        
        if (createSpecialPermissionIfNotExists("DONNEES_SALARIALES", PermissionAction.LISTER_SENSIBLE, 
                "Permet de lister et voir les informations salariales", 
                "Lister Données Salariales")) {
            newPermissionsCount++;
        }

        return newPermissionsCount;
    }

    /**
     * Scans the application context for all classes annotated with @Entity
     */
    private Set<Class<?>> scanForEntityClasses() {
        Set<Class<?>> entityClasses = new HashSet<>();
        
        // Get all entities from EntityManager metamodel
        entityManager.getMetamodel().getEntities().forEach(entityType -> {
            Class<?> javaType = entityType.getJavaType();
            if (javaType.isAnnotationPresent(Entity.class)) {
                entityClasses.add(javaType);
            }
        });
        
        return entityClasses;
    }

    /**
     * Gets the entity name from the class, using @Table annotation if available
     */
    private String getEntityName(Class<?> entityClass) {
        // Check if @Table annotation exists and has a name
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            if (tableAnnotation.name() != null && !tableAnnotation.name().isEmpty()) {
                return tableAnnotation.name().toUpperCase();
            }
        }
        
        // Use class name in uppercase as resource name
        return entityClass.getSimpleName().toUpperCase();
    }

    /**
     * Creates a permission if it doesn't already exist (idempotent operation)
     */
    private boolean createPermissionIfNotExists(String ressource, PermissionAction action) {
        try {
            // Check if permission already exists
            if (permissionRepository.existsByRessourceAndAction(ressource, action)) {
                return false;
            }

            // Determine the module for this resource
            Module assignedModule = Module.fromRessource(ressource);
            
            // Create new permission
            Permission permission = new Permission();
            permission.setRessource(ressource);
            permission.setAction(action);
            permission.setDescription(generateDescription(ressource, action));
            permission.setNomAffichage(generateDisplayName(ressource, action));
            permission.setModule(assignedModule);
            
            try {
                permissionRepository.save(permission);
                return true;
            } catch (Exception e) {
                try {
                    permission.setModule(Module.SYSTEME);
                    permissionRepository.save(permission);
                    return true;
                } catch (Exception fallbackException) {
                    log.error("❌ Failed to create permission even with fallback module: {}", fallbackException.getMessage());
                    return false;
                }
            }
        } catch (Exception outerException) {
            // Handle case where the constraint violation happens during the existence check
            // Skip this permission creation but don't fail the entire initialization
            return false;
        }
    }

    /**
     * Creates a special permission with custom description
     */
    private boolean createSpecialPermissionIfNotExists(String ressource, PermissionAction action, 
                                                       String description, String nomAffichage) {
        if (permissionRepository.existsByRessourceAndAction(ressource, action)) {
            return false;
        }

        Module assignedModule = Module.fromRessource(ressource);
        
        Permission permission = new Permission();
        permission.setRessource(ressource);
        permission.setAction(action);
        permission.setDescription(description);
        permission.setNomAffichage(nomAffichage);
        permission.setModule(assignedModule);
        
        try {
            permissionRepository.save(permission);
            return true;
        } catch (Exception e) {
            try {
                permission.setModule(Module.SYSTEME);
                permissionRepository.save(permission);
                return true;
            } catch (Exception fallbackException) {
                return false;
            }
        }
    }

    /**
     * Generates a French description for the permission
     */
    private String generateDescription(String ressource, PermissionAction action) {
        String entityName = formatEntityName(ressource);
        
        return switch (action) {
            case CREER -> "Permet de créer des " + entityName;
            case LIRE -> "Permet de voir/lire les " + entityName;
            case MODIFIER -> "Permet de modifier les " + entityName;
            case SUPPRIMER -> "Permet de supprimer les " + entityName;
            case EXPORTER -> "Permet d'exporter les " + entityName;
            case IMPORTER -> "Permet d'importer les " + entityName;
            case LISTER_SENSIBLE -> "Permet de lister les données sensibles de " + entityName;
        };
    }

    /**
     * Generates a display name for the UI
     */
    private String generateDisplayName(String ressource, PermissionAction action) {
        String entityName = formatEntityNameForDisplay(ressource);
        String actionLabel = ACTION_LABELS.getOrDefault(action, action.name());
        return actionLabel + " " + entityName;
    }

    /**
     * Formats entity name for descriptions (lowercase plural)
     */
    private String formatEntityName(String ressource) {
        return ressource.toLowerCase().replace("_", " ");
    }

    /**
     * Formats entity name for display (title case)
     */
    private String formatEntityNameForDisplay(String ressource) {
        String[] words = ressource.split("_");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (result.length() > 0) result.append(" ");
            result.append(word.substring(0, 1).toUpperCase())
                  .append(word.substring(1).toLowerCase());
        }
        
        return result.toString();
    }
}
