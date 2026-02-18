package com.example.BacK.domain.g_Utilisateurs;

/**
 * Enum représentant les différents modules métier de l'application.
 * Utilisé pour regrouper les permissions par module dans l'interface utilisateur.
 */
public enum Module {
    GESTION_UTILISATEURS("Gestion des Utilisateurs", "USER_MANAGEMENT"),
    GESTION_CLIENTS("Gestion des Clients", "CLIENT_MANAGEMENT"),
    GESTION_FOURNISSEURS("Gestion des Fournisseurs", "SUPPLIER_MANAGEMENT"),
    GESTION_PROJETS("Gestion des Projets", "PROJECT_MANAGEMENT"),
    GESTION_RH("Gestion des Ressources Humaines", "HR_MANAGEMENT"),
    GESTION_FORMATIONS("Gestion des Formations", "TRAINING_MANAGEMENT"),
    GESTION_VEHICULES("Gestion des Véhicules", "VEHICLE_MANAGEMENT"),
    GESTION_STOCK("Gestion du Stock", "STOCK_MANAGEMENT"),
    SYSTEME("Système et Configuration", "SYSTEM");

    private final String displayName;
    private final String code;

    Module(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
    public static Module fromRessource(String ressource) {
        if (ressource == null) return SYSTEME;
        
        String upper = ressource.toUpperCase();
        
        // HR Management Module - Handle all HR-related entities (check BEFORE projects!)
        // IMPORTANT: Employee entities should be in HR, not Projects
        if (upper.contains("EMPLOYEE") || upper.contains("EMPLOYE") || upper.contains("CONGEE") || 
            upper.contains("FICHE") || upper.contains("PRIME") || upper.contains("REGLE") || 
            upper.contains("RETENUE") || upper.contains("PAIE")) {
            return GESTION_RH;
        }
        
        // Client Management Module - Handle all client-related entities
        if (upper.contains("CLIENT") || upper.contains("FACTURE") || upper.contains("PAIEMENT") ||
            upper.contains("INTERACTION") || upper.contains("CONTACT")) {
            return GESTION_CLIENTS;
        }
        
        // Supplier Management Module - Handle all supplier-related entities
        if (upper.contains("FOURNISSEUR") || upper.contains("DETTE") || upper.contains("TRANCHE")) {
            return GESTION_FOURNISSEURS;
        }
        
        // Project Management Module - Handle project-related entities (but NOT employee entities)
        if (upper.contains("PROJET") || upper.contains("TACHE") || upper.contains("PHASE") || 
            upper.contains("MISSION") || upper.contains("CHARGE") || upper.contains("LIVRABLE") ||
            upper.contains("COMMENTAIRE") || upper.contains("PROBLEME")) {
            return GESTION_PROJETS;
        }
        
        // Special case for EMPLOYEAFFECTE - this is project assignment, so it goes to projects
        if (upper.contains("EMPLOYEAFFECTE") || upper.contains("AFFECTE")) {
            return GESTION_PROJETS;
        }
        
        // Formation Management Module - Handle all formation-related entities
        if (upper.contains("FORMATION") || upper.contains("CATEGORIE") || upper.contains("CERTIFICAT") ||
            upper.contains("CHAPITRE") || upper.contains("CONTENU") || upper.contains("DOMAINE") ||
            upper.contains("EVALUATION") || upper.contains("SEANCE") || upper.contains("USERFORMATION") ||
            upper.contains("SOUSCATEGORIE") || upper.contains("FORMATIONLITE")) {
            return GESTION_FORMATIONS;
        }
        
        // Handle the generic "TYPE" entity - assume it's formation-related based on context
        if (upper.equals("TYPE")) {
            return GESTION_FORMATIONS;
        }
        
        // Vehicle Management Module - Handle all vehicle-related entities  
        if (upper.contains("VEHICULE") || upper.contains("CARTE") || upper.contains("CARBURANT") ||
            upper.contains("REPARATION") || upper.contains("TRANSACTION") || upper.contains("TELEPEAGE") ||
            upper.contains("PRIX") || upper.contains("GAZOIL")) {
            return GESTION_VEHICULES;
        }
        
        // Stock Management Module - Handle all stock-related entities
        if (upper.contains("CATEGORIE_ARTICLE") || upper.contains("CATEGORY") || upper.contains("ARTICLE") ||
            upper.contains("MARQUE") || upper.contains("ENTREPOT") || upper.contains("STOCK") || 
            upper.contains("INVENTORY") || upper.contains("INVENTAIRE")) {
            return GESTION_STOCK;
        }
        
        // System Module - Handle system entities including user management, and other modules
        if (upper.equals("USER") || upper.equals("ROLE") || upper.equals("PERMISSION") ||
            upper.contains("DONNEES_") || upper.contains("DOSSIER") ||
            upper.contains("PRET") || upper.contains("SOP") || upper.contains("SOUS_TRAITANCE")) {
            return SYSTEME;
        }
        
        // Default to SYSTEM for unrecognized entities
        return SYSTEME;
    }
}
