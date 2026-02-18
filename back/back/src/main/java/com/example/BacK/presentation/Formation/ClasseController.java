package com.example.BacK.presentation.Formation;

import com.example.BacK.domain.g_Formation.Classe;
import com.example.BacK.infrastructure.services.g_Formation.ClasseRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/classes")
@Tag(name = "Classe", description = "Gestion des classes (groupes d'apprenants)")
public class ClasseController {

    private final ClasseRepositoryService classeService;

    public ClasseController(ClasseRepositoryService classeService) {
        this.classeService = classeService;
    }

    // ==================== CRUD Operations ====================

    @Operation(summary = "Obtenir toutes les classes")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllClasses() {
        List<Classe> classes = classeService.getAllClasses();
        List<Map<String, Object>> response = classes.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir une classe par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClasseById(@PathVariable Long id) {
        return classeService.getClasseById(id)
                .map(classe -> ResponseEntity.ok(mapToResponse(classe)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Rechercher des classes avec pagination")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchClasses(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {

        Page<Classe> pageResult = classeService.searchClasses(search, page, size, sortBy, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent().stream().map(this::mapToResponse).toList());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir les classes d'une formation")
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<Map<String, Object>>> getClassesByFormation(@PathVariable Long formationId) {
        List<Classe> classes = classeService.getClassesByFormation(formationId);
        List<Map<String, Object>> response = classes.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir les classes d'un plan de formation")
    @GetMapping("/plan-formation/{planFormationId}")
    public ResponseEntity<List<Map<String, Object>>> getClassesByPlanFormation(@PathVariable Long planFormationId) {
        List<Classe> classes = classeService.getClassesByPlanFormation(planFormationId);
        List<Map<String, Object>> response = classes.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir les classes actives")
    @GetMapping("/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveClasses() {
        List<Classe> classes = classeService.getActiveClasses();
        List<Map<String, Object>> response = classes.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtenir les classes avec places disponibles")
    @GetMapping("/available")
    public ResponseEntity<List<Map<String, Object>>> getClassesWithAvailableSpots() {
        List<Classe> classes = classeService.getClassesWithAvailableSpots();
        List<Map<String, Object>> response = classes.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Créer une nouvelle classe")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createClasse(@RequestBody Map<String, Object> payload) {
        String nom = (String) payload.get("nom");
        String description = (String) payload.get("description");
        Integer capaciteMax = payload.get("capaciteMax") != null
                ? ((Number) payload.get("capaciteMax")).intValue()
                : null;
        Long formationId = payload.get("formationId") != null
                ? ((Number) payload.get("formationId")).longValue()
                : null;
        Long planFormationId = payload.get("planFormationId") != null
                ? ((Number) payload.get("planFormationId")).longValue()
                : null;
        LocalDate dateDebutAcces = payload.get("dateDebutAcces") != null
                ? LocalDate.parse((String) payload.get("dateDebutAcces"))
                : null;
        LocalDate dateFinAcces = payload.get("dateFinAcces") != null
                ? LocalDate.parse((String) payload.get("dateFinAcces"))
                : null;

        if (nom == null || nom.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le nom est requis"));
        }

        try {
            Classe classe = classeService.createClasse(nom, description, capaciteMax, formationId, planFormationId, dateDebutAcces, dateFinAcces);
            log.info("Classe créée: {} ({})", classe.getNom(), classe.getCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(classe));
        } catch (Exception e) {
            log.error("Erreur lors de la création de la classe", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Mettre à jour une classe")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClasse(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {

        String nom = (String) payload.get("nom");
        String description = (String) payload.get("description");
        Integer capaciteMax = payload.get("capaciteMax") != null
                ? ((Number) payload.get("capaciteMax")).intValue()
                : null;
        Long formationId = payload.get("formationId") != null
                ? ((Number) payload.get("formationId")).longValue()
                : null;
        Long planFormationId = payload.get("planFormationId") != null
                ? ((Number) payload.get("planFormationId")).longValue()
                : null;
        Boolean isActive = (Boolean) payload.get("isActive");
        LocalDate dateDebutAcces = payload.get("dateDebutAcces") != null
                ? LocalDate.parse((String) payload.get("dateDebutAcces"))
                : null;
        LocalDate dateFinAcces = payload.get("dateFinAcces") != null
                ? LocalDate.parse((String) payload.get("dateFinAcces"))
                : null;

        try {
            Classe classe = classeService.updateClasse(id, nom, description, capaciteMax, formationId, planFormationId, isActive, dateDebutAcces, dateFinAcces);
            log.info("Classe mise à jour: {}", id);
            return ResponseEntity.ok(mapToResponse(classe));
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la classe", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Supprimer une classe")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        try {
            classeService.deleteClasse(id);
            log.info("Classe supprimée: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la classe", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Activer/Désactiver une classe")
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable Long id) {
        try {
            Classe classe = classeService.toggleActive(id);
            log.info("Classe {} - active toggled to {}", id, classe.getIsActive());
            return ResponseEntity.ok(mapToResponse(classe));
        } catch (Exception e) {
            log.error("Erreur toggle active", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== Apprenant Management ====================

    @Operation(summary = "Ajouter un apprenant à une classe")
    @PostMapping("/{classeId}/apprenants/{apprenantId}")
    public ResponseEntity<Map<String, Object>> addApprenantToClasse(
            @PathVariable Long classeId,
            @PathVariable Long apprenantId) {
        try {
            Classe classe = classeService.addApprenantToClasse(classeId, apprenantId);
            log.info("Apprenant {} ajouté à la classe {}", apprenantId, classeId);
            return ResponseEntity.ok(mapToResponse(classe));
        } catch (Exception e) {
            log.error("Erreur ajout apprenant", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Retirer un apprenant d'une classe")
    @DeleteMapping("/{classeId}/apprenants/{apprenantId}")
    public ResponseEntity<Map<String, Object>> removeApprenantFromClasse(
            @PathVariable Long classeId,
            @PathVariable Long apprenantId) {
        try {
            Classe classe = classeService.removeApprenantFromClasse(classeId, apprenantId);
            log.info("Apprenant {} retiré de la classe {}", apprenantId, classeId);
            return ResponseEntity.ok(mapToResponse(classe));
        } catch (Exception e) {
            log.error("Erreur retrait apprenant", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Ajouter plusieurs apprenants à une classe")
    @PostMapping("/{classeId}/apprenants")
    public ResponseEntity<Map<String, Object>> addMultipleApprenants(
            @PathVariable Long classeId,
            @RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> apprenantIds = (List<Number>) payload.get("apprenantIds");
            
            if (apprenantIds == null || apprenantIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "apprenantIds is required and cannot be empty"));
            }
            
            List<Long> ids = apprenantIds.stream()
                    .filter(n -> n != null)
                    .map(Number::longValue)
                    .toList();

            Classe classe = classeService.addMultipleApprenantsToClasse(classeId, ids);
            log.info("{} apprenants ajoutés à la classe {}", ids.size(), classeId);
            return ResponseEntity.ok(mapToResponse(classe));
        } catch (Exception e) {
            log.error("Erreur ajout multiple apprenants", e);
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", errorMsg);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "Obtenir les classes d'un apprenant")
    @GetMapping("/apprenant/{apprenantId}")
    public ResponseEntity<List<Map<String, Object>>> getClassesByApprenant(@PathVariable Long apprenantId) {
        List<Classe> classes = classeService.getClassesByApprenant(apprenantId);
        List<Map<String, Object>> response = classes.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ==================== Statistics ====================

    @Operation(summary = "Obtenir les statistiques des classes")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", classeService.countTotalClasses());
        stats.put("active", classeService.countActiveClasses());
        return ResponseEntity.ok(stats);
    }

    // ==================== Helper Methods ====================

    private Map<String, Object> mapToResponse(Classe classe) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", classe.getId());
        map.put("nom", classe.getNom());
        map.put("code", classe.getCode());
        map.put("description", classe.getDescription());
        map.put("capaciteMax", classe.getCapaciteMax());
        map.put("isActive", classe.getIsActive());
        map.put("dateDebutAcces", classe.getDateDebutAcces());
        map.put("dateFinAcces", classe.getDateFinAcces());
        map.put("isAccessible", classe.isAccessible());
        map.put("createdAt", classe.getCreatedAt());
        map.put("updatedAt", classe.getUpdatedAt());
        map.put("enrollmentCount", classe.getEnrollmentCount());
        map.put("isFull", classe.isFull());

        // Formation info
        if (classe.getFormation() != null) {
            Map<String, Object> formationMap = new HashMap<>();
            formationMap.put("id", classe.getFormation().getIdFormation());
            formationMap.put("theme", classe.getFormation().getTheme());
            map.put("formation", formationMap);
            map.put("formationId", classe.getFormation().getIdFormation());
            map.put("formationTheme", classe.getFormation().getTheme());
        }

        // Plan formation info
        if (classe.getPlanFormation() != null) {
            Map<String, Object> planMap = new HashMap<>();
            planMap.put("id", classe.getPlanFormation().getIdPlanFormation());
            planMap.put("titre", classe.getPlanFormation().getTitre());
            map.put("planFormation", planMap);
            map.put("planFormationId", classe.getPlanFormation().getIdPlanFormation());
            map.put("planFormationTitre", classe.getPlanFormation().getTitre());

            // Also include formation from plan
            if (classe.getPlanFormation().getFormation() != null) {
                map.put("formationId", classe.getPlanFormation().getFormation().getIdFormation());
                map.put("formationTheme", classe.getPlanFormation().getFormation().getTheme());
            }
        }

        // Apprenants (basic info only)
        if (classe.getApprenants() != null && !classe.getApprenants().isEmpty()) {
            List<Map<String, Object>> apprenantsList = classe.getApprenants().stream()
                    .map(a -> {
                        Map<String, Object> aMap = new HashMap<>();
                        aMap.put("id", a.getId());
                        aMap.put("nom", a.getNom());
                        aMap.put("prenom", a.getPrenom());
                        aMap.put("email", a.getEmail());
                        aMap.put("matricule", a.getMatricule());
                        return aMap;
                    })
                    .toList();
            map.put("apprenants", apprenantsList);
        } else {
            map.put("apprenants", List.of());
        }

        return map;
    }
}
