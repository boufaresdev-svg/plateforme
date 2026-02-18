package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Apprenant.addApprenant.AddApprenantCommand;
import com.example.BacK.application.g_Formation.Command.Apprenant.deleteApprenant.DeleteApprenantCommand;
import com.example.BacK.application.g_Formation.Command.Apprenant.updateApprenant.UpdateApprenantCommand;
import com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantClasses.GetApprenantClassesQuery;
import com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantClasses.GetApprenantClassesResponse;
import com.example.BacK.application.g_Formation.Query.Apprenant.CheckEnrollment.CheckApprenantEnrollmentQuery;
import com.example.BacK.application.g_Formation.Query.Apprenant.CheckEnrollment.CheckApprenantEnrollmentResponse;
import com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantsByPlanFormation.GetApprenantsByPlanFormationQuery;
import com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantsByPlanFormation.GetApprenantsByPlanFormationResponse;
import com.example.BacK.application.g_Formation.Query.Apprenant.GetApprenantQuery;
import com.example.BacK.application.g_Formation.Query.Apprenant.GetApprenantResponse;
import com.example.BacK.application.mediator.Mediator;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.enumEntity.StatusInscription;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/apprenants")
@Tag(name = "Apprenant", description = "Gestion des apprenants (création, modification, consultation, suppression, import CSV)")
public class ApprenantController {

    private final Mediator mediator;
    private final ApprenantRepositoryService apprenantService;
    private final PlanFormationRepositoryService planFormationService;

    public ApprenantController(Mediator mediator, 
                               ApprenantRepositoryService apprenantService,
                               PlanFormationRepositoryService planFormationService) {
        this.mediator = mediator;
        this.apprenantService = apprenantService;
        this.planFormationService = planFormationService;
    }

    // ==================== CRUD Operations ====================

    @Operation(summary = "Obtenir la liste des apprenants",
            description = "Retourne la liste complète des apprenants")
    @GetMapping
    public ResponseEntity<List<GetApprenantResponse>> getAllApprenants() {
        List<GetApprenantResponse> responses = mediator.sendToHandlers(new GetApprenantQuery(null));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtenir un apprenant par ID",
            description = "Retourne les informations d'un apprenant spécifique")
    @GetMapping("/{id}")
    public ResponseEntity<GetApprenantResponse> getApprenantById(@PathVariable Long id) {
        GetApprenantResponse response = (GetApprenantResponse) mediator.sendToHandlers(new GetApprenantQuery(id));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Rechercher des apprenants avec pagination",
            description = "Recherche des apprenants par nom, prénom, email ou matricule avec pagination")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchApprenants(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        
        Page<Apprenant> pageResult = apprenantService.searchApprenants(search, page, size, sortBy, sortDirection);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent().stream().map(this::mapToResponse).toList());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Vérifier l'existence d'un apprenant",
            description = "Vérifie si un apprenant existe par email ou matricule")
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyApprenant(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String matricule) {
        
        if ((email == null || email.isEmpty()) && (matricule == null || matricule.isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of(
                "exists", false,
                "error", "Veuillez fournir un email ou un matricule"
            ));
        }
        
        boolean exists = false;
        Apprenant apprenant = null;
        
        if (email != null && !email.isEmpty()) {
            apprenant = apprenantService.findByEmail(email).orElse(null);
            exists = apprenant != null;
        } else if (matricule != null && !matricule.isEmpty()) {
            apprenant = apprenantService.findByMatricule(matricule).orElse(null);
            exists = apprenant != null;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        if (exists && apprenant != null) {
            response.put("apprenant", mapToResponse(apprenant));
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir les classes/cours d'un apprenant",
            description = "Retourne la liste des classes dans lesquelles un apprenant est inscrit")
    @GetMapping("/{id}/classes")
    public ResponseEntity<GetApprenantClassesResponse> getApprenantClasses(@PathVariable Long id) {
        try {
            List<GetApprenantClassesResponse> responses = mediator.sendToHandlers(
                    new GetApprenantClassesQuery(id)
            );
            if (responses.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(responses.get(0));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Vérifier si un apprenant est inscrit dans une classe",
            description = "Vérifie si un apprenant spécifique est inscrit dans une classe donnée")
    @GetMapping("/{apprenantId}/enrolled/{classeId}")
    public ResponseEntity<CheckApprenantEnrollmentResponse> checkApprenantEnrollment(
            @PathVariable Long apprenantId,
            @PathVariable Long classeId) {
        
        List<CheckApprenantEnrollmentResponse> responses = mediator.sendToHandlers(
                new CheckApprenantEnrollmentQuery(apprenantId, classeId)
        );
        
        if (responses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        CheckApprenantEnrollmentResponse response = responses.get(0);
        if (response.getError() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir les apprenants d'un plan de formation",
            description = "Retourne la liste des apprenants associés à un plan de formation donné")
    @GetMapping("/plan/{planFormationId}")
    public ResponseEntity<List<GetApprenantsByPlanFormationResponse>> getApprenantsByPlanFormation(
            @PathVariable Long planFormationId) {
        List<GetApprenantsByPlanFormationResponse> responses =
                mediator.sendToHandlers(new GetApprenantsByPlanFormationQuery(planFormationId));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtenir les apprenants d'une formation",
            description = "Retourne la liste des apprenants inscrits à une formation (via tous les plans)")
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<Map<String, Object>>> getApprenantsByFormation(@PathVariable Long formationId) {
        List<Apprenant> apprenants = apprenantService.findByFormationId(formationId);
        List<Map<String, Object>> response = apprenants.stream().map(this::mapToResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir les apprenants d'une formation avec pagination",
            description = "Retourne la liste paginée des apprenants inscrits à une formation")
    @GetMapping("/formation/{formationId}/paged")
    public ResponseEntity<Map<String, Object>> getApprenantsByFormationPaged(
            @PathVariable Long formationId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Page<Apprenant> pageResult = apprenantService.findByFormationIdPaged(formationId, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent().stream().map(this::mapToResponse).toList());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Créer un nouvel apprenant",
            description = "Ajoute un nouvel apprenant avec génération automatique du matricule et mot de passe")
    @PostMapping
    public ResponseEntity<Object> addApprenant(@Valid @RequestBody AddApprenantCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Mettre à jour un apprenant",
            description = "Modifie les informations d'un apprenant existant")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateApprenant(@PathVariable Long id,
                                                @Valid @RequestBody UpdateApprenantCommand command) {
        command.setIdApprenant(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer un apprenant",
            description = "Supprime un apprenant existant de la base de données")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprenant(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteApprenantCommand(id));
        return ResponseEntity.noContent().build();
    }

    // ==================== User Management Features ====================

    @Operation(summary = "Bloquer un apprenant",
            description = "Bloque l'accès d'un apprenant au système")
    @PatchMapping("/{id}/block")
    public ResponseEntity<Map<String, Object>> blockApprenant(@PathVariable Long id) {
        Apprenant apprenant = apprenantService.blockApprenant(id);
        log.info("Apprenant {} bloqué", id);
        return ResponseEntity.ok(mapToResponse(apprenant));
    }

    @Operation(summary = "Débloquer un apprenant",
            description = "Rétablit l'accès d'un apprenant au système")
    @PatchMapping("/{id}/unblock")
    public ResponseEntity<Map<String, Object>> unblockApprenant(@PathVariable Long id) {
        Apprenant apprenant = apprenantService.unblockApprenant(id);
        log.info("Apprenant {} débloqué", id);
        return ResponseEntity.ok(mapToResponse(apprenant));
    }

    @Operation(summary = "Basculer le statut staff",
            description = "Active ou désactive le statut staff d'un apprenant")
    @PatchMapping("/{id}/toggle-staff")
    public ResponseEntity<Map<String, Object>> toggleStaff(@PathVariable Long id) {
        Apprenant apprenant = apprenantService.toggleStaff(id);
        log.info("Apprenant {} - staff toggled to {}", id, apprenant.getIsStaff());
        return ResponseEntity.ok(mapToResponse(apprenant));
    }

    @Operation(summary = "Changer le mot de passe d'un apprenant",
            description = "Définit un nouveau mot de passe pour un apprenant")
    @PatchMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String newPassword = payload.get("password");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe est requis"));
        }
        apprenantService.updatePassword(id, newPassword);
        log.info("Mot de passe mis à jour pour l'apprenant {}", id);
        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));
    }

    @Operation(summary = "Générer un mot de passe pour un apprenant",
            description = "Génère un mot de passe aléatoire et l'assigne à l'apprenant")
    @PostMapping("/{id}/generate-password")
    public ResponseEntity<Map<String, String>> generatePassword(@PathVariable Long id) {
        String generatedPassword = apprenantService.generatePassword();
        apprenantService.updatePassword(id, generatedPassword);
        log.info("Mot de passe généré pour l'apprenant {}", id);
        // Return the plain password so it can be communicated to the user
        return ResponseEntity.ok(Map.of(
            "message", "Mot de passe généré avec succès",
            "password", generatedPassword
        ));
    }

    // ==================== CSV Import ====================

    @Operation(summary = "Importer des apprenants depuis un fichier CSV",
            description = "Importe une liste d'apprenants depuis un fichier CSV. Colonnes attendues: nom, prenom, email, telephone, adresse, prerequis")
    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importFromCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long planFormationId,
            @RequestParam(required = false, defaultValue = "true") boolean generatePasswords) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est vide"));
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("text/csv") && !contentType.contains("csv"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être au format CSV"));
        }

        List<Apprenant> importedApprenants = new ArrayList<>();
        List<Map<String, String>> errors = new ArrayList<>();
        List<Map<String, String>> generatedCredentials = new ArrayList<>();
        int lineNumber = 1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build())) {

            for (CSVRecord record : csvParser) {
                lineNumber++;
                try {
                    String email = record.get("email");
                    
                    // Check if email already exists
                    if (apprenantService.existsByEmail(email)) {
                        errors.add(Map.of(
                            "line", String.valueOf(lineNumber),
                            "error", "Email déjà existant: " + email
                        ));
                        continue;
                    }

                    Apprenant apprenant = new Apprenant();
                    apprenant.setNom(record.get("nom"));
                    apprenant.setPrenom(getValueOrNull(record, "prenom"));
                    apprenant.setEmail(email);
                    apprenant.setTelephone(getValueOrNull(record, "telephone"));
                    apprenant.setAdresse(getValueOrNull(record, "adresse"));
                    apprenant.setPrerequis(getValueOrNull(record, "prerequis"));
                    apprenant.setStatusInscription(StatusInscription.INSCRIT);
                    apprenant.setIsActive(true);
                    apprenant.setIsBlocked(false);
                    apprenant.setIsStaff(false);

                    // Generate password if requested
                    String plainPassword = null;
                    if (generatePasswords) {
                        plainPassword = apprenantService.generatePassword();
                        apprenant.setPassword(plainPassword);
                    }

                    // Link to plan formation if provided
                    if (planFormationId != null) {
                        planFormationService.getPlanFormationById(planFormationId)
                                .ifPresent(apprenant::setPlanFormation);
                    }

                    Apprenant saved = apprenantService.saveApprenant(apprenant);
                    importedApprenants.add(saved);

                    // Store credentials for response
                    if (plainPassword != null) {
                        generatedCredentials.add(Map.of(
                            "matricule", saved.getMatricule(),
                            "email", saved.getEmail(),
                            "password", plainPassword
                        ));
                    }

                } catch (Exception e) {
                    errors.add(Map.of(
                        "line", String.valueOf(lineNumber),
                        "error", e.getMessage()
                    ));
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'import CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la lecture du fichier: " + e.getMessage()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("imported", importedApprenants.size());
        response.put("errors", errors);
        if (generatePasswords) {
            response.put("credentials", generatedCredentials);
        }

        log.info("Import CSV: {} apprenants importés, {} erreurs", importedApprenants.size(), errors.size());
        return ResponseEntity.ok(response);
    }

    // ==================== Statistics ====================

    @Operation(summary = "Obtenir les statistiques des apprenants",
            description = "Retourne des statistiques sur les apprenants (total, actifs, bloqués)")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", apprenantService.countTotalApprenants());
        stats.put("active", apprenantService.countActiveApprenants());
        stats.put("blocked", apprenantService.countBlockedApprenants());
        return ResponseEntity.ok(stats);
    }

    // ==================== Helper Methods ====================

    private String getValueOrNull(CSVRecord record, String column) {
        try {
            String value = record.get(column);
            return (value != null && !value.isEmpty()) ? value : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Map<String, Object> mapToResponse(Apprenant apprenant) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", apprenant.getId());
        map.put("matricule", apprenant.getMatricule());
        map.put("nom", apprenant.getNom());
        map.put("prenom", apprenant.getPrenom());
        map.put("email", apprenant.getEmail());
        map.put("telephone", apprenant.getTelephone());
        map.put("adresse", apprenant.getAdresse());
        map.put("prerequis", apprenant.getPrerequis());
        map.put("statusInscription", apprenant.getStatusInscription());
        map.put("isBlocked", apprenant.getIsBlocked());
        map.put("isStaff", apprenant.getIsStaff());
        map.put("isActive", apprenant.getIsActive());
        map.put("createdAt", apprenant.getCreatedAt());
        map.put("updatedAt", apprenant.getUpdatedAt());
        map.put("lastLogin", apprenant.getLastLogin());
        
        if (apprenant.getPlanFormation() != null) {
            Map<String, Object> planMap = new HashMap<>();
            planMap.put("idPlanFormation", apprenant.getPlanFormation().getIdPlanFormation());
            planMap.put("titre", apprenant.getPlanFormation().getTitre());
            map.put("planFormation", planMap);
        }
        
        return map;
    }
}
