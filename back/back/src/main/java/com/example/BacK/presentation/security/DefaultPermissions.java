package com.example.BacK.presentation.security;

import com.example.BacK.domain.g_Utilisateurs.Permission;
import com.example.BacK.domain.g_Utilisateurs.PermissionAction;
import com.example.BacK.infrastructure.repository.g_Utilisateur.PermissionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes default permissions for all entities in the system
 * COMMENTED OUT - Uncomment when ready to auto-initialize permissions
 */
// @Component
@RequiredArgsConstructor
public class DefaultPermissions implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    // Define all resources/entities in your application organized by modules
    private static final String[] RESOURCES = {
        // Client Management Module
        "CLIENT",
        "CONTACTCLIENT",
        "FACTURECLIENT", 
        "INTERACTIONCLIENT",
        "PAIEMENTCLIENT",
        
        // Supplier Management Module
        "FOURNISSEUR",
        "DETTESFOURNISSEUR",
        "TRANCHEPAIEMENT",
        
        // Project Management Module
        "PROJET",
        "TACHE",
        "PHASE",
        "MISSION",
        "CHARGE",
        "COMMENTAIRETACHE",
        "EMPLOYEAFFECTE",
        "LIVRABLETACHE",
        "PROBLEMETACHE",
        
        // Human Resources Module
        "EMPLOYEE",
        "CONGEE", 
        "FICHEPAIE",
        "PRIME",
        "REGLE",
        "RETENUE",
        
        // Vehicle Management Module
        "VEHICULE",
        "REPARATION",
        "CARTEGAZOIL",
        "CARTETELEPEAGE",
        "CANALCOMMUNICATION",
        "PRIXCARBURANT",
        "TRANSACTIONCARBURANT",
        
        // Formation Module
        "FORMATION",
        "FORMATIONLITE",
        "SEANCE",
        "CATEGORIE",
        "SOUSCATEGORIE",
        "DOMAINE",
        "TYPE",
        "CHAPITRE",
        "CONTENU",
        "EVALUATION",
        "CERTIFICAT",
        "USERFORMATION",
        
        // User Management Module
        "USER",
        "ROLE",
        "PERMISSION",
        
        // Other Modules
        "STOCK",
        "DOSSIER",
        "PRET",
        "SOP",
        "SOUS_TRAITANCE"
    };

    // Display names in French for each resource
    private static final java.util.Map<String, String> RESOURCE_DISPLAY_NAMES = java.util.Map.ofEntries(
        // Client Management Module
        java.util.Map.entry("CLIENT", "Client"),
        java.util.Map.entry("CONTACTCLIENT", "Contact Client"),
        java.util.Map.entry("FACTURECLIENT", "Facture Client"),
        java.util.Map.entry("INTERACTIONCLIENT", "Interaction Client"),
        java.util.Map.entry("PAIEMENTCLIENT", "Paiement Client"),
        
        // Supplier Management Module
        java.util.Map.entry("FOURNISSEUR", "Fournisseur"),
        java.util.Map.entry("DETTESFOURNISSEUR", "Dettes Fournisseur"),
        java.util.Map.entry("TRANCHEPAIEMENT", "Tranche de Paiement"),
        
        // Project Management Module
        java.util.Map.entry("PROJET", "Projet"),
        java.util.Map.entry("TACHE", "Tâche"),
        java.util.Map.entry("PHASE", "Phase"),
        java.util.Map.entry("MISSION", "Mission"),
        java.util.Map.entry("CHARGE", "Charge"),
        java.util.Map.entry("COMMENTAIRETACHE", "Commentaire Tâche"),
        java.util.Map.entry("EMPLOYEAFFECTE", "Employé Affecté"),
        java.util.Map.entry("LIVRABLETACHE", "Livrable Tâche"),
        java.util.Map.entry("PROBLEMETACHE", "Problème Tâche"),
        
        // Human Resources Module
        java.util.Map.entry("EMPLOYEE", "Employé"),
        java.util.Map.entry("CONGEE", "Congé"),
        java.util.Map.entry("FICHEPAIE", "Fiche de Paie"),
        java.util.Map.entry("PRIME", "Prime"),
        java.util.Map.entry("REGLE", "Règle"),
        java.util.Map.entry("RETENUE", "Retenue"),
        
        // Vehicle Management Module
        java.util.Map.entry("VEHICULE", "Véhicule"),
        java.util.Map.entry("REPARATION", "Réparation"),
        java.util.Map.entry("CARTEGAZOIL", "Carte Gazoil"),
        java.util.Map.entry("CARTETELEPEAGE", "Carte Télépéage"),
        java.util.Map.entry("CANALCOMMUNICATION", "Canal Communication"),
        java.util.Map.entry("PRIXCARBURANT", "Prix Carburant"),
        java.util.Map.entry("TRANSACTIONCARBURANT", "Transaction Carburant"),
        
        // Formation Module
        java.util.Map.entry("FORMATION", "Formation"),
        java.util.Map.entry("FORMATIONLITE", "Formation Lite"),
        java.util.Map.entry("SEANCE", "Séance"),
        java.util.Map.entry("CATEGORIE", "Catégorie"),
        java.util.Map.entry("SOUSCATEGORIE", "Sous-catégorie"),
        java.util.Map.entry("DOMAINE", "Domaine"),
        java.util.Map.entry("TYPE", "Type"),
        java.util.Map.entry("CHAPITRE", "Chapitre"),
        java.util.Map.entry("CONTENU", "Contenu"),
        java.util.Map.entry("EVALUATION", "Évaluation"),
        java.util.Map.entry("CERTIFICAT", "Certificat"),
        java.util.Map.entry("USERFORMATION", "Formation Utilisateur"),
        
        // User Management Module
        java.util.Map.entry("USER", "Utilisateur"),
        java.util.Map.entry("ROLE", "Rôle"),
        java.util.Map.entry("PERMISSION", "Permission"),
        
        // Other Modules
        java.util.Map.entry("STOCK", "Stock"),
        java.util.Map.entry("DOSSIER", "Dossier"),
        java.util.Map.entry("PRET", "Prêt"),
        java.util.Map.entry("SOP", "SOP"),
        java.util.Map.entry("SOUS_TRAITANCE", "Sous-traitance")
    );

    // Define entities that contain sensitive information
    private static final String[] SENSITIVE_ENTITIES = {
        // HR sensitive data
        "EMPLOYEE",
        "FICHEPAIE",
        "PRIME", 
        "RETENUE",
        
        // Financial sensitive data
        "DETTESFOURNISSEUR",
        "TRANCHEPAIEMENT",
        "PAIEMENTCLIENT",
        "FACTURECLIENT",
        
        // User management sensitive data
        "USER",
        "ROLE",
        "PERMISSION"
    };

    // Action display names in French
    private static final java.util.Map<PermissionAction, String> ACTION_DISPLAY_NAMES = java.util.Map.of(
        PermissionAction.CREER, "Créer",
        PermissionAction.LIRE, "Consulter",
        PermissionAction.MODIFIER, "Modifier",
        PermissionAction.SUPPRIMER, "Supprimer",
        PermissionAction.EXPORTER, "Exporter",
        PermissionAction.IMPORTER, "Importer",
        PermissionAction.LISTER_SENSIBLE, "Lister (Sensible)"
    );

    @Override
    public void run(String... args) throws Exception {
        List<Permission> permissionsToCreate = new ArrayList<>();

        for (String resource : RESOURCES) {
            for (PermissionAction action : PermissionAction.values()) {
                // Skip LISTER_SENSIBLE for non-sensitive entities
                if (action == PermissionAction.LISTER_SENSIBLE && !isSensitiveEntity(resource)) {
                    continue;
                }
                
                // Check if permission already exists
                if (!permissionRepository.existsByRessourceAndAction(resource, action)) {
                    Permission permission = new Permission();
                    permission.setRessource(resource);
                    permission.setAction(action);
                    permission.setNomAffichage(generateDisplayName(resource, action));
                    permission.setDescription(generateDescription(resource, action));
                    permissionsToCreate.add(permission);
                }
            }
        }

        if (!permissionsToCreate.isEmpty()) {
            permissionRepository.saveAll(permissionsToCreate);
        }
    }

    private boolean isSensitiveEntity(String resource) {
        for (String sensitiveEntity : SENSITIVE_ENTITIES) {
            if (sensitiveEntity.equals(resource)) {
                return true;
            }
        }
        return false;
    }

    private String generateDisplayName(String resource, PermissionAction action) {
        String resourceName = RESOURCE_DISPLAY_NAMES.getOrDefault(resource, resource);
        String actionName = ACTION_DISPLAY_NAMES.getOrDefault(action, action.name());
        return actionName + " " + resourceName;
    }

    private String generateDescription(String resource, PermissionAction action) {
        String resourceName = RESOURCE_DISPLAY_NAMES.getOrDefault(resource, resource);
        
        return switch (action) {
            case CREER -> "Permet de créer de nouveaux " + resourceName.toLowerCase();
            case LIRE -> "Permet de consulter les " + resourceName.toLowerCase();
            case MODIFIER -> "Permet de modifier les " + resourceName.toLowerCase();
            case SUPPRIMER -> "Permet de supprimer les " + resourceName.toLowerCase();
            case EXPORTER -> "Permet d'exporter les données des " + resourceName.toLowerCase();
            case IMPORTER -> "Permet d'importer des données " + resourceName.toLowerCase();
            case LISTER_SENSIBLE -> "Permet de lister des données sensibles des " + resourceName.toLowerCase();
        };
    }
}
