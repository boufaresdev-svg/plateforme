package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.ObjectifGlobal.create.CreateObjectifGlobalCommand;
import com.example.BacK.application.g_Formation.Command.ObjectifGlobal.create.CreateObjectifGlobalHandler;
import com.example.BacK.application.g_Formation.Command.ObjectifGlobal.deleteObjectifGlobal.DeleteObjectifGlobalCommand;
import com.example.BacK.application.g_Formation.Command.ObjectifGlobal.deleteObjectifGlobal.DeleteObjectifGlobalHandler;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objectifsglobaux")
@Tag(name = "Objectif Global", description = "Gestion des objectifs globaux de formation")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, allowCredentials = "true")
public class ObjectifGlobalController {

    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final CreateObjectifGlobalHandler createObjectifGlobalHandler;
    private final DeleteObjectifGlobalHandler deleteObjectifGlobalHandler;

    public ObjectifGlobalController(ObjectifGlobalRepository objectifGlobalRepository,
                                   CreateObjectifGlobalHandler createObjectifGlobalHandler,
                                   DeleteObjectifGlobalHandler deleteObjectifGlobalHandler) {
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.createObjectifGlobalHandler = createObjectifGlobalHandler;
        this.deleteObjectifGlobalHandler = deleteObjectifGlobalHandler;
    }

    @Operation(summary = "Obtenir tous les objectifs globaux")
    @GetMapping
    public ResponseEntity<List<ObjectifGlobal>> getAllObjectifsGlobaux() {
        List<ObjectifGlobal> objectifs = objectifGlobalRepository.findAll();
        return ResponseEntity.ok(objectifs);
    }

    @Operation(summary = "Obtenir un objectif global par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ObjectifGlobal> getObjectifGlobalById(@PathVariable Long id) {
        return objectifGlobalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtenir les objectifs globaux d'une formation")
    @GetMapping("/formation/{idFormation}")
    public ResponseEntity<List<ObjectifGlobal>> getObjectifsGlobauxByFormation(@PathVariable Long idFormation) {
        List<ObjectifGlobal> objectifs = objectifGlobalRepository.findByFormations_IdFormation(idFormation);
        return ResponseEntity.ok(objectifs);
    }

    @Operation(summary = "Rechercher des objectifs globaux par mot-clé", 
               description = "Recherche dans le libellé, la description et les tags. Case-insensitive.")
    @GetMapping("/search")
    public ResponseEntity<List<ObjectifGlobal>> searchObjectifsGlobaux(
            @RequestParam(required = true) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(objectifGlobalRepository.findAll());
        }
        List<ObjectifGlobal> results = objectifGlobalRepository.searchByKeyword(keyword.trim());
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Obtenir les objectifs non liés à une formation",
               description = "Retourne tous les objectifs globaux qui ne sont pas encore liés à la formation spécifiée")
    @GetMapping("/not-linked/{formationId}")
    public ResponseEntity<List<ObjectifGlobal>> getObjectifsNotLinkedToFormation(@PathVariable Long formationId) {
        List<ObjectifGlobal> objectifs = objectifGlobalRepository.findNotLinkedToFormation(formationId);
        return ResponseEntity.ok(objectifs);
    }

    @Operation(summary = "Rechercher des objectifs non liés à une formation",
               description = "Recherche par mot-clé parmi les objectifs qui ne sont pas encore liés à la formation")
    @GetMapping("/search-not-linked/{formationId}")
    public ResponseEntity<List<ObjectifGlobal>> searchObjectifsNotLinkedToFormation(
            @PathVariable Long formationId,
            @RequestParam(required = true) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(objectifGlobalRepository.findNotLinkedToFormation(formationId));
        }
        List<ObjectifGlobal> results = objectifGlobalRepository.searchByKeywordNotLinkedToFormation(
                keyword.trim(), formationId);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Créer un nouvel objectif global et le lier à une formation")
    @PostMapping
    public ResponseEntity<ObjectifGlobal> createObjectifGlobal(@RequestBody CreateObjectifGlobalCommand command) {
        ObjectifGlobal saved = createObjectifGlobalHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Mettre à jour un objectif global")
    @PutMapping("/{id}")
    public ResponseEntity<ObjectifGlobal> updateObjectifGlobal(
            @PathVariable Long id,
            @RequestBody ObjectifGlobal objectifGlobal) {
        
        return objectifGlobalRepository.findById(id)
                .map(existing -> {
                    existing.setLibelle(objectifGlobal.getLibelle());
                    existing.setDescription(objectifGlobal.getDescription());
                    ObjectifGlobal updated = objectifGlobalRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un objectif global")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjectifGlobal(@PathVariable Long id) {
        try {
            DeleteObjectifGlobalCommand command = new DeleteObjectifGlobalCommand(id);
            deleteObjectifGlobalHandler.handle(command);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
