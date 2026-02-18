package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Formation.addFormation.AddFormationCommand;
import com.example.BacK.application.g_Formation.Command.Formation.deleteFormation.DeleteFormationCommand;
import com.example.BacK.application.g_Formation.Command.Formation.updateFormation.UpdateFormationCommand;
import com.example.BacK.application.g_Formation.Command.Formation.duplicateFormation.DuplicateFormationCommand;
import com.example.BacK.application.g_Formation.Command.Formation.duplicateFormation.DuplicateFormationResponse;
import com.example.BacK.application.g_Formation.Command.Formation.linkObjectifGlobal.LinkObjectifGlobalCommand;
import com.example.BacK.application.g_Formation.Command.Formation.unlinkObjectifGlobal.UnlinkObjectifGlobalCommand;
import com.example.BacK.application.g_Formation.Command.Formation.linkObjectifSpecifique.LinkObjectifSpecifiqueCommand;
import com.example.BacK.application.g_Formation.Command.Formation.unlinkObjectifSpecifique.UnlinkObjectifSpecifiqueCommand;
import com.example.BacK.application.g_Formation.Query.formation.formation.GetFormationQuery;
import com.example.BacK.application.g_Formation.Query.formation.formation.GetFormationResponse;
import com.example.BacK.application.g_Formation.Query.formation.formation.GetFormationPagedQuery;
import com.example.BacK.application.g_Formation.Query.formation.formation.PagedFormationResponse;
import com.example.BacK.application.g_Formation.Query.formation.statistics.FormationStatisticsResponse;
import com.example.BacK.application.g_Formation.Query.formation.statistics.FormationStatisticsService;
import com.example.BacK.application.mediator.Mediator;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.infrastructure.services.g_Formation.FormationRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/formations")
@Tag(name = "Formation", description = "Gestion des formations (création, mise à jour, suppression, consultation)")
public class FormationController {

    private final Mediator mediator;
    private final FormationStatisticsService statisticsService;
    private final FormationRepositoryService formationRepositoryService;
    
    private static final String UPLOAD_DIR = "uploads/formations/";

    public FormationController(Mediator mediator, FormationStatisticsService statisticsService, 
                               FormationRepositoryService formationRepositoryService) {
        this.mediator = mediator;
        this.statisticsService = statisticsService;
        this.formationRepositoryService = formationRepositoryService;
    }

    @Operation(
            summary = "Obtenir les statistiques des formations",
            description = "Retourne des statistiques complètes sur les formations, participants, certifications, etc."
    )
    @GetMapping("/statistics")
    public ResponseEntity<FormationStatisticsResponse> getStatistics() {
        FormationStatisticsResponse stats = statisticsService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "Obtenir la liste des formations",
            description = "Retourne toutes les formations disponibles dans le système"
    )
    @GetMapping
    public ResponseEntity<List<GetFormationResponse>> getAllFormations() {
        List<GetFormationResponse> formations = mediator.sendToHandlers(new GetFormationQuery());
        return ResponseEntity.ok(formations);
    }

    @Operation(
            summary = "Obtenir la liste paginée des formations",
            description = "Retourne une page de formations avec pagination et tri"
    )
    @GetMapping("/paginated")
    public ResponseEntity<PagedFormationResponse> getFormationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idFormation") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        GetFormationPagedQuery query = new GetFormationPagedQuery(page, size);
        query.setSortBy(sortBy);
        query.setSortDirection(sortDirection);
        List<?> responses = mediator.sendToHandlers(query);
        if (!responses.isEmpty() && responses.get(0) instanceof PagedFormationResponse) {
            return ResponseEntity.ok((PagedFormationResponse) responses.get(0));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Obtenir une formation par ID",
            description = "Retourne les détails d'une formation spécifique"
    )
    @GetMapping("/{id}")
    public ResponseEntity<List<GetFormationResponse>> getFormationById(@PathVariable Long id) {
        List<GetFormationResponse> result = mediator.sendToHandlers(new GetFormationQuery(id));
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Créer une nouvelle formation",
            description = "Ajoute une nouvelle formation avec ses informations et relations"
    )
    @PostMapping
    public ResponseEntity<Object> addFormation(@Valid @RequestBody AddFormationCommand command) {
        Object response = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Mettre à jour une formation",
            description = "Modifie les informations d’une formation existante"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFormation(@PathVariable Long id,
                                                @Valid @RequestBody UpdateFormationCommand command) {
        command.setIdFormation(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer une formation",
            description = "Supprime une formation existante de la base de données"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteFormationCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Dupliquer une formation",
            description = "Crée une copie complète d'une formation avec tous ses objectifs globaux, objectifs spécifiques, et contenus détaillés"
    )
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Object> duplicateFormation(@PathVariable Long id) {
        Object response = mediator.sendToHandlers(new DuplicateFormationCommand(id));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lier un objectif global à une formation",
            description = "Ajoute un objectif global à la liste des objectifs d'une formation"
    )
    @PostMapping("/{formationId}/link-objectif-global/{objectifGlobalId}")
    public ResponseEntity<Void> linkObjectifGlobal(
            @PathVariable Long formationId,
            @PathVariable Long objectifGlobalId) {
        mediator.sendToHandlers(new LinkObjectifGlobalCommand(formationId, objectifGlobalId));
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Délier un objectif global d'une formation",
            description = "Retire un objectif global de la liste des objectifs d'une formation"
    )
    @DeleteMapping("/{formationId}/unlink-objectif-global/{objectifGlobalId}")
    public ResponseEntity<Void> unlinkObjectifGlobal(
            @PathVariable Long formationId,
            @PathVariable Long objectifGlobalId) {
        mediator.sendToHandlers(new UnlinkObjectifGlobalCommand(formationId, objectifGlobalId));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Lier un objectif spécifique à une formation",
            description = "Ajoute un objectif spécifique à la liste des objectifs d'une formation"
    )
    @PostMapping("/{formationId}/link-objectif-specifique/{objectifSpecifiqueId}")
    public ResponseEntity<Void> linkObjectifSpecifique(
            @PathVariable Long formationId,
            @PathVariable Long objectifSpecifiqueId) {
        mediator.sendToHandlers(new LinkObjectifSpecifiqueCommand(formationId, objectifSpecifiqueId));
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Délier un objectif spécifique d'une formation",
            description = "Retire un objectif spécifique de la liste des objectifs d'une formation"
    )
    @DeleteMapping("/{formationId}/unlink-objectif-specifique/{objectifSpecifiqueId}")
    public ResponseEntity<Void> unlinkObjectifSpecifique(
            @PathVariable Long formationId,
            @PathVariable Long objectifSpecifiqueId) {
        mediator.sendToHandlers(new UnlinkObjectifSpecifiqueCommand(formationId, objectifSpecifiqueId));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Téléverser une image pour une formation",
            description = "Upload une image pour une formation existante (JPG, PNG, GIF, WebP - max 5 Mo)"
    )
    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFormationImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        // Validate formation exists
        Formation formation = formationRepositoryService.getFormationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'ID : " + id));
        
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est vide"));
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") 
                && !contentType.equals("image/png") 
                && !contentType.equals("image/jpg")
                && !contentType.equals("image/gif")
                && !contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Type de fichier non autorisé. Utilisez JPG, PNG, GIF ou WebP."));
        }
        
        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est trop volumineux (max 5 Mo)"));
        }
        
        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String newFilename = "formation_" + id + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update formation with image URL
            String imageUrl = "/uploads/formations/" + newFilename;
            formation.setImageUrl(imageUrl);
            formationRepositoryService.saveFormation(formation);
            
            log.info("Image uploaded for formation {}: {}", id, imageUrl);
            
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
            
        } catch (IOException e) {
            log.error("Error uploading image for formation {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'enregistrement du fichier"));
        }
    }
}
