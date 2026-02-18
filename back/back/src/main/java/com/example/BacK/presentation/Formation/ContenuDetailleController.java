package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Query.ContenuDetaille.ContenuWithJourResponse;
import com.example.BacK.application.g_Formation.Command.ContenuDetaille.addContenuDetaille.AddContenuDetailleCommand;
import com.example.BacK.application.g_Formation.Command.ContenuDetaille.ContentLevelDto;
import com.example.BacK.application.g_Formation.Command.ContenuDetaille.deleteContenuDetaille.DeleteContenuDetailleCommand;
import com.example.BacK.application.g_Formation.Command.ContenuDetaille.updateContenuDetaille.UpdateContenuDetailleCommand;
import com.example.BacK.application.g_Formation.Query.ContenuDetaille.getAllByJour.GetAllContenuDetailleByJourQuery;
import com.example.BacK.application.g_Formation.Query.ContenuDetaille.getContenuDetailleById.GetContenuDetailleByIdQuery;
import com.example.BacK.application.g_Formation.Query.ContenuDetaille.paged.GetPagedContenuDetailleQuery;
import com.example.BacK.application.g_Formation.Query.ContenuDetaille.paged.PagedContenuDetailleResponse;
import com.example.BacK.application.mediator.Mediator;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.domain.g_Formation.ContenuJourNiveauAssignment;
import com.example.BacK.domain.g_Formation.ContentLevel;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.LevelFile;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuJourFormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.PlanFormationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.hibernate.Hibernate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/contenus-detailles")
@Tag(name = "Contenu Détaillé", description = "Gestion des contenus détaillés de formations avec support multi-niveau")
public class ContenuDetailleController {

    private final Mediator mediator;
    private final ContenuDetailleRepositoryService repositoryService;
    private final ContenuJourFormationRepository contenuJourFormationRepository;
    private final FormationRepository formationRepository;
    private final PlanFormationRepository planFormationRepository;

    @Value("${file.upload-dir:uploads/contenus}")
    private String uploadDir;

    public ContenuDetailleController(Mediator mediator, 
                                      ContenuDetailleRepositoryService repositoryService,
                                      ContenuJourFormationRepository contenuJourFormationRepository,
                                      FormationRepository formationRepository,
                                      PlanFormationRepository planFormationRepository) {
        this.mediator = mediator;
        this.repositoryService = repositoryService;
        this.contenuJourFormationRepository = contenuJourFormationRepository;
        this.formationRepository = formationRepository;
        this.planFormationRepository = planFormationRepository;
    }

    /**
     * Create a new ContenuDetaille with multi-level content support
     */
    @Operation(
            summary = "Créer un contenu détaillé",
            description = "Ajoute un nouveau contenu détaillé avec support de 5 niveaux de contenu"
    )
    @PostMapping
    public ResponseEntity<Object> addContenuDetaille(
            @Valid @RequestBody AddContenuDetailleCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Get ContenuDetaille by ID
     */
    @Operation(
            summary = "Obtenir un contenu détaillé par ID",
            description = "Retourne les détails d'un contenu détaillé spécifique avec tous ses niveaux"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Object> getContenuDetailleById(@PathVariable Long id) {
        Object response = mediator.sendToHandlers(new GetContenuDetailleByIdQuery(id));
        return ResponseEntity.ok(response);
    }

    /**
     * Get all ContenuDetaille (independent of jour)
     */
    @Operation(
            summary = "Obtenir tous les contenus détaillés",
            description = "Retourne tous les contenus détaillés disponibles"
    )
    @GetMapping
    public ResponseEntity<?> getAllContenuDetaille() {
        List<?> responses = mediator.sendToHandlers(
                new com.example.BacK.application.g_Formation.Query.ContenuDetaille.getAll.GetAllContenuDetailleQuery()
        );
        // Mediator returns List containing the handler's response
        // Since handler returns List<GetAllContenuDetailleResponse>, we get List<List<...>>
        // We need to unwrap it
        if (!responses.isEmpty()) {
            return ResponseEntity.ok(responses.get(0));
        }
        return ResponseEntity.ok(responses);
    }

    /**
     * Get paginated ContenuDetaille
     */
    @Operation(
            summary = "Obtenir les contenus détaillés paginés",
            description = "Retourne une page de contenus détaillés avec pagination, tri optionnel, et filtrage par jour"
    )
    @GetMapping("/paginated")
    public ResponseEntity<PagedContenuDetailleResponse> getContenuDetaillePagedQ(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idContenuDetaille") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) Long idJourFormation) {
        GetPagedContenuDetailleQuery query = new GetPagedContenuDetailleQuery(page, size);
        query.setSortBy(sortBy);
        query.setSortDirection(sortDirection);
        query.setIdJourFormation(idJourFormation);
        Object result = mediator.sendToHandlers(query);

        // The mediator may return either the handler result directly or a List containing it
        if (result instanceof PagedContenuDetailleResponse response) {
            return ResponseEntity.ok(response);
        }

        if (result instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof PagedContenuDetailleResponse response) {
            return ResponseEntity.ok(response);
        }

        // Fallback: return empty page instead of 404 to keep frontend logic simple
        PagedContenuDetailleResponse empty = new PagedContenuDetailleResponse(
                List.of(), page, size, 0, 0, false, false
        );
        return ResponseEntity.ok(empty);
    }

    /**
     * Search ContenuDetaille by titre (case-insensitive, substring)
     */
    @Operation(
            summary = "Rechercher des contenus détaillés",
            description = "Recherche par titre (contient, insensible à la casse). Retourne tous les contenus si la requête est vide."
    )
    @GetMapping("/search")
    public ResponseEntity<List<ContenuDetaille>> searchContenuDetaille(@RequestParam(name = "q", required = false) String query) {
        List<ContenuDetaille> results = repositoryService.searchContenuDetaille(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Get all ContenuDetaille for a specific Jour
     */
    @Operation(
            summary = "Obtenir les contenus d'une journée de formation",
            description = "Retourne tous les contenus détaillés associés à une journée de formation"
    )
    @GetMapping("/by-jour/{idJour}")
    public ResponseEntity<List<Object>> getContenuDetailleByJour(@PathVariable Long idJour) {
        List<Object> responses = mediator.sendToHandlers(
                new GetAllContenuDetailleByJourQuery(idJour)
        );
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all ContenuDetaille for a specific Formation
     */
    @Operation(
            summary = "Obtenir les contenus d'une formation",
            description = "Retourne tous les contenus détaillés associés à une formation spécifique"
    )
    @GetMapping("/by-formation/{idFormation}")
    public ResponseEntity<List<ContenuDetaille>> getContenuDetailleByFormation(@PathVariable Long idFormation) {
        List<ContenuDetaille> contenus = repositoryService.getContenuDetailleByFormation(idFormation);
        return ResponseEntity.ok(contenus);
    }

    /**
     * Get all ContenuDetaille for a specific Formation WITH jour mapping
     * Returns contenus with their associated jour number for PDF generation
     */
    @Operation(
            summary = "Obtenir les contenus d'une formation avec jours",
            description = "Retourne tous les contenus détaillés avec leur numéro de jour pour la génération PDF"
    )
    @GetMapping("/by-formation/{idFormation}/with-jours")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ContenuWithJourResponse>> getContenuDetailleByFormationWithJours(@PathVariable Long idFormation) {
        List<ContenuWithJourResponse> result = new ArrayList<>();
        
        // First, get the formation with its objectifs to filter properly
        var formationOpt = formationRepository.findByIdWithObjectifs(idFormation);
        if (formationOpt.isEmpty()) {
            return ResponseEntity.ok(result); // Return empty if formation not found
        }
        
        Formation formation = formationOpt.get();

        // Collect plan formation IDs (from formation entity and repository to avoid lazy issues)
        Set<Long> planIds = new HashSet<>();
        if (formation.getPlanFormations() != null) {
            formation.getPlanFormations().forEach(pf -> {
                if (pf != null && pf.getIdPlanFormation() != null) {
                    planIds.add(pf.getIdPlanFormation());
                }
            });
        }
        planFormationRepository.findByFormation_IdFormation(idFormation).forEach(pf -> {
            if (pf != null && pf.getIdPlanFormation() != null) {
                planIds.add(pf.getIdPlanFormation());
            }
        });

        // Collect ObjectifSpecifique IDs (fallback path)
        Set<Long> objectifSpecIds = new HashSet<>();
        if (formation.getObjectifsSpecifiques() != null) {
            for (ObjectifSpecifique os : formation.getObjectifsSpecifiques()) {
                if (os != null && os.getIdObjectifSpec() != null) {
                    objectifSpecIds.add(os.getIdObjectifSpec());
                }
            }
        }

        // Gather all ContenuJourFormation linked to this formation
        List<ContenuJourFormation> cjfs = new ArrayList<>();

        // Preferred: by plan formation
        if (!planIds.isEmpty()) {
            for (Long planId : planIds) {
                List<ContenuJourFormation> planCjfs = contenuJourFormationRepository.findByPlanFormationOrderByOrdre(planId);
                // Initialize levels for each ContenuDetaille
                for (ContenuJourFormation cjf : planCjfs) {
                    if (cjf.getContenuAssignments() != null) {
                        Hibernate.initialize(cjf.getContenuAssignments());
                        cjf.getContenuAssignments().forEach(ca -> {
                            if (ca.getContenuDetaille() != null) {
                                Hibernate.initialize(ca.getContenuDetaille());
                                if (ca.getContenuDetaille().getLevels() != null) {
                                    Hibernate.initialize(ca.getContenuDetaille().getLevels());
                                    ca.getContenuDetaille().getLevels().forEach(level -> {
                                        if (level.getFiles() != null) {
                                            Hibernate.initialize(level.getFiles());
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                cjfs.addAll(planCjfs);
            }
        }

        // Fallback: by objectif spécifique if no CJF via plan
        if (cjfs.isEmpty() && !objectifSpecIds.isEmpty()) {
            List<Long> allCjfIds = contenuJourFormationRepository.findAllIds();
            for (Long cjfId : allCjfIds) {
                contenuJourFormationRepository.findByIdWithContenusDetailles(cjfId).ifPresent(cjf -> {
                    if (cjf.getObjectifSpecifique() != null &&
                        objectifSpecIds.contains(cjf.getObjectifSpecifique().getIdObjectifSpec())) {
                        // Initialize levels for each ContenuDetaille
                        if (cjf.getContenuAssignments() != null) {
                            cjf.getContenuAssignments().forEach(ca -> {
                                if (ca.getContenuDetaille() != null && ca.getContenuDetaille().getLevels() != null) {
                                    Hibernate.initialize(ca.getContenuDetaille().getLevels());
                                    ca.getContenuDetaille().getLevels().forEach(level -> {
                                        if (level.getFiles() != null) {
                                            Hibernate.initialize(level.getFiles());
                                        }
                                    });
                                }
                            });
                        }
                        cjfs.add(cjf);
                    }
                });
            }
        }

        // Build responses
        for (ContenuJourFormation cjf : cjfs) {
            Integer numeroJour = cjf.getNumeroJour();
            // Fallback to jour 1 if numeroJour is null (data not set in DB)
            if (numeroJour == null) {
                numeroJour = 1;
            }
            Long idContenuJour = cjf.getIdContenuJour();

            if (cjf.getContenuAssignments() != null) {
                for (ContenuJourNiveauAssignment assignment : cjf.getContenuAssignments()) {
                    ContenuDetaille cd = assignment.getContenuDetaille();
                    if (cd != null) {
                        ContenuWithJourResponse response = new ContenuWithJourResponse();
                        response.setIdContenuDetaille(cd.getIdContenuDetaille());
                        response.setTitre(cd.getTitre());
                        response.setMethodesPedagogiques(cd.getMethodesPedagogiques());
                        response.setTags(cd.getTags());
                        response.setDureeTheorique(cd.getDureeTheorique());
                        response.setDureePratique(cd.getDureePratique());
                        response.setContenusCles(cd.getContenusCles());
                        response.setNumeroJour(numeroJour);
                        response.setIdContenuJour(idContenuJour);

                        // Extract files from levels
                        List<ContenuWithJourResponse.FileInfo> files = new ArrayList<>();
                        if (cd.getLevels() != null) {
                            for (ContentLevel level : cd.getLevels()) {
                                if (level.getFiles() != null) {
                                    for (LevelFile file : level.getFiles()) {
                                        ContenuWithJourResponse.FileInfo fileInfo = new ContenuWithJourResponse.FileInfo();
                                        fileInfo.setFileName(file.getFileName());
                                        fileInfo.setFilePath(file.getFilePath());
                                        fileInfo.setFileType(file.getFileType());
                                        fileInfo.setFileSize(file.getFileSize());
                                        fileInfo.setLevelNumber(level.getLevelNumber());
                                        files.add(fileInfo);
                                    }
                                }
                            }
                        }
                        response.setFiles(files);

                        result.add(response);
                    }
                }
            }
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Update an existing ContenuDetaille
     */
    @Operation(
            summary = "Mettre à jour un contenu détaillé",
            description = "Modifie les informations et les niveaux d'un contenu détaillé"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateContenuDetaille(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, Object> requestBody) {
        
        // Manually construct command to handle proper type conversion
        UpdateContenuDetailleCommand command = new UpdateContenuDetailleCommand();
        command.setIdContenuDetaille(id);
        command.setTitre((String) requestBody.get("titre"));
        command.setMethodesPedagogiques((String) requestBody.get("methodesPedagogiques"));
        command.setTags((String) requestBody.get("tags"));
        
        if (requestBody.get("contenusCles") != null) {
            command.setContenusCles((List<String>) requestBody.get("contenusCles"));
        }
        
        if (requestBody.get("dureeTheorique") != null) {
            Object dureeTheo = requestBody.get("dureeTheorique");
            if (dureeTheo instanceof Number) {
                command.setDureeTheorique(((Number) dureeTheo).doubleValue());
            }
        }
        
        if (requestBody.get("dureePratique") != null) {
            Object dureePrat = requestBody.get("dureePratique");
            if (dureePrat instanceof Number) {
                command.setDureePratique(((Number) dureePrat).doubleValue());
            }
        }
        
        if (requestBody.get("idJourFormation") != null) {
            Object idJour = requestBody.get("idJourFormation");
            if (idJour instanceof Number) {
                command.setIdJourFormation(((Number) idJour).longValue());
            }
        }
        
        // Convert levels from maps to ContentLevelDto objects
        if (requestBody.get("levels") != null) {
            List<Map<String, Object>> levelsData = (List<Map<String, Object>>) requestBody.get("levels");
            List<ContentLevelDto> levels = new ArrayList<>();
            
            for (Map<String, Object> levelData : levelsData) {
                ContentLevelDto level = new ContentLevelDto();
                
                if (levelData.get("levelNumber") != null) {
                    level.setLevelNumber(((Number) levelData.get("levelNumber")).intValue());
                }
                
                level.setTheorieContent((String) levelData.get("theorieContent"));
                
                if (levelData.get("dureeTheorique") != null) {
                    Object dureeTheo = levelData.get("dureeTheorique");
                    if (dureeTheo instanceof Number) {
                        level.setDureeTheorique(((Number) dureeTheo).doubleValue());
                    }
                }
                
                if (levelData.get("dureePratique") != null) {
                    Object dureePrat = levelData.get("dureePratique");
                    if (dureePrat instanceof Number) {
                        level.setDureePratique(((Number) dureePrat).doubleValue());
                    }
                }
                
                levels.add(level);
            }
            
            command.setLevels(levels);
        }
        
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.accepted().body(result);
    }

    /**
     * Delete a ContenuDetaille
     */
    @Operation(
            summary = "Supprimer un contenu détaillé",
            description = "Supprime un contenu détaillé et tous ses niveaux associés"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContenuDetaille(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteContenuDetailleCommand(id));
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload multiple files for a specific level of ContenuDetaille
     */
    @Operation(
            summary = "Uploader plusieurs fichiers pour un niveau",
            description = "Upload plusieurs fichiers (PDF, vidéo, document, etc.) pour un niveau spécifique (1-3)"
    )
    @PostMapping("/{id}/levels/{levelNumber}/upload")
    public ResponseEntity<Map<String, Object>> uploadLevelFiles(
            @PathVariable Long id,
            @PathVariable Integer levelNumber,
            @RequestParam("files") List<MultipartFile> files) {

        try {
            // Validate level number (1-3 for Débutant, Intermédiaire, Avancé)
            if (levelNumber < 1 || levelNumber > 3) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le numéro de niveau doit être entre 1 et 3"));
            }

            // Get the contenu detaille from database
            ContenuDetaille contenuDetaille = repositoryService.getContenuDetailleById(id)
                    .orElseThrow(() -> new RuntimeException("Contenu détaillé non trouvé avec l'ID: " + id));

            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Find the target level
            ContentLevel targetLevel = null;
            for (ContentLevel level : contenuDetaille.getLevels()) {
                if (level.getLevelNumber() != null && level.getLevelNumber().equals(levelNumber)) {
                    targetLevel = level;
                    break;
                }
            }

            if (targetLevel == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Niveau " + levelNumber + " non trouvé dans ce contenu"));
            }

            List<Map<String, Object>> uploadedFiles = new ArrayList<>();

            // Upload all files
            for (MultipartFile file : files) {
                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : "";
                String uniqueFilename = "contenu_" + id + "_level_" + levelNumber + "_" + UUID.randomUUID() + fileExtension;

                // Save file to disk
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Create LevelFile object
                LevelFile levelFile = new LevelFile();
                levelFile.setFileName(originalFilename);
                levelFile.setFilePath(uniqueFilename);
                levelFile.setFileType(file.getContentType());
                levelFile.setFileSize(file.getSize());
                levelFile.setUploadDate(java.time.LocalDateTime.now());

                // Add to level's files
                targetLevel.getFiles().add(levelFile);

                // Prepare response
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileName", originalFilename);
                fileInfo.put("filePath", uniqueFilename);
                fileInfo.put("fileType", file.getContentType());
                fileInfo.put("fileSize", file.getSize());
                uploadedFiles.add(fileInfo);
            }

            // Save to database
            repositoryService.saveContenuDetaille(contenuDetaille);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", files.size() + " fichier(s) uploadé(s) avec succès");
            response.put("files", uploadedFiles);
            response.put("levelNumber", levelNumber);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload du fichier: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    /**
     * Download/View a file from a specific level
     */
    @Operation(
            summary = "Télécharger un fichier d'un niveau",
            description = "Télécharge ou affiche le fichier associé à un niveau"
    )
    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String filename,
            @RequestParam(value = "download", required = false, defaultValue = "false") boolean download) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                // Always use 'inline' to prevent download prompt
                // For videos and images, add headers to discourage downloading
                String disposition = download ? "attachment" : "inline";
                
                var responseBuilder = ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0")
                        .header("X-Content-Type-Options", "nosniff");
                
                // Add headers to discourage downloading for media files
                if (contentType.startsWith("video/") || contentType.startsWith("image/")) {
                    responseBuilder = responseBuilder
                            .header("Content-Security-Policy", "default-src 'self'")
                            .header("X-Frame-Options", "SAMEORIGIN");
                }
                
                return responseBuilder.body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * View video/image in embedded player (no download)
     */
    @Operation(
            summary = "Visualiser un fichier média sans téléchargement",
            description = "Affiche une vidéo ou image dans un lecteur intégré qui empêche le téléchargement"
    )
    @GetMapping("/view/{filename}")
    public ResponseEntity<String> viewMediaFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType = Files.probeContentType(filePath);
            String fileUrl = "/api/contenus-detailles/files/" + filename;
            
            String html;
            if (contentType != null && contentType.startsWith("video/")) {
                html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>%s</title>
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { background: #000; display: flex; justify-content: center; align-items: center; min-height: 100vh; }
                            .title { color: #fff; padding: 10px; font-size: 14px; background: rgba(0,0,0,0.7); position: fixed; top: 0; left: 0; right: 0; z-index: 100; font-family: Arial, sans-serif; }
                            video { max-width: 100%%; max-height: 90vh; }
                        </style>
                    </head>
                    <body oncontextmenu="return false;">
                        <div class="title">%s</div>
                        <video controls controlsList="nodownload noplaybackrate" disablePictureInPicture oncontextmenu="return false;">
                            <source src="%s" type="%s">
                            Votre navigateur ne supporte pas la lecture vidéo.
                        </video>
                        <script>
                            document.addEventListener('keydown', function(e) {
                                if ((e.ctrlKey && (e.key === 's' || e.key === 'S' || e.key === 'u' || e.key === 'U')) || e.key === 'F12') {
                                    e.preventDefault();
                                }
                            });
                        </script>
                    </body>
                    </html>
                    """.formatted(filename, filename, fileUrl, contentType);
            } else if (contentType != null && contentType.startsWith("image/")) {
                html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>%s</title>
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { background: #1a1a1a; display: flex; justify-content: center; align-items: center; min-height: 100vh; }
                            .title { color: #fff; padding: 10px; font-size: 14px; background: rgba(0,0,0,0.5); position: fixed; top: 0; left: 0; right: 0; font-family: Arial, sans-serif; }
                            img { max-width: 95vw; max-height: 90vh; object-fit: contain; pointer-events: none; user-select: none; -webkit-user-drag: none; }
                        </style>
                    </head>
                    <body oncontextmenu="return false;" ondragstart="return false;">
                        <div class="title">%s</div>
                        <img src="%s" alt="%s" />
                        <script>
                            document.addEventListener('keydown', function(e) {
                                if ((e.ctrlKey && (e.key === 's' || e.key === 'S' || e.key === 'u' || e.key === 'U')) || e.key === 'F12') {
                                    e.preventDefault();
                                }
                            });
                        </script>
                    </body>
                    </html>
                    """.formatted(filename, filename, fileUrl, filename);
            } else {
                // For other files, redirect to download
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, fileUrl)
                        .build();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header("X-Frame-Options", "DENY")
                    .header("Content-Security-Policy", "default-src 'self' 'unsafe-inline'; media-src 'self'; img-src 'self'")
                    .body(html);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a specific file from a level
     */
    @Operation(
            summary = "Supprimer un fichier spécifique d'un niveau",
            description = "Supprime un fichier spécifique d'un niveau par son chemin"
    )
    @DeleteMapping("/{id}/levels/{levelNumber}/files/{filePath}")
    public ResponseEntity<Map<String, String>> deleteLevelFile(
            @PathVariable Long id,
            @PathVariable Integer levelNumber,
            @PathVariable String filePath) {
        
        try {
            // Validate level number
            if (levelNumber < 1 || levelNumber > 3) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le numéro de niveau doit être entre 1 et 3"));
            }

            // Get the contenu detaille from database
            ContenuDetaille contenuDetaille = repositoryService.getContenuDetailleById(id)
                    .orElseThrow(() -> new RuntimeException("Contenu détaillé non trouvé avec l'ID: " + id));

            // Find the level and remove the specific file
            boolean fileRemoved = false;
            for (ContentLevel level : contenuDetaille.getLevels()) {
                if (level.getLevelNumber() != null && level.getLevelNumber().equals(levelNumber)) {
                    // Find and remove the file from the list
                    fileRemoved = level.getFiles().removeIf(file -> file.getFilePath().equals(filePath));
                    break;
                }
            }

            if (!fileRemoved) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Fichier non trouvé dans le niveau " + levelNumber));
            }

            // Delete physical file from disk
            try {
                Path uploadPath = Paths.get(uploadDir);
                Path fileToDelete = uploadPath.resolve(filePath);
                Files.deleteIfExists(fileToDelete);
            } catch (IOException e) {
            }

            // Save to database
            repositoryService.saveContenuDetaille(contenuDetaille);

            return ResponseEntity.ok(Map.of("message", "Fichier supprimé avec succès"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
    
    /**
     * Get all files for a specific level
     */
    @Operation(
            summary = "Obtenir tous les fichiers d'un niveau",
            description = "Retourne la liste de tous les fichiers d'un niveau spécifique"
    )
    @GetMapping("/{id}/levels/{levelNumber}/files")
    public ResponseEntity<List<Map<String, Object>>> getLevelFiles(
            @PathVariable Long id,
            @PathVariable Integer levelNumber) {
        
        try {
            // Get the contenu detaille from database
            ContenuDetaille contenuDetaille = repositoryService.getContenuDetailleById(id)
                    .orElseThrow(() -> new RuntimeException("Contenu détaillé non trouvé avec l'ID: " + id));

            // Find the level
            for (ContentLevel level : contenuDetaille.getLevels()) {
                if (level.getLevelNumber() != null && level.getLevelNumber().equals(levelNumber)) {
                    List<Map<String, Object>> filesInfo = level.getFiles().stream().map(file -> {
                        Map<String, Object> info = new HashMap<>();
                        info.put("fileName", file.getFileName());
                        info.put("filePath", file.getFilePath());
                        info.put("fileType", file.getFileType());
                        info.put("fileSize", file.getFileSize());
                        info.put("uploadDate", file.getUploadDate());
                        info.put("description", file.getDescription());
                        return info;
                    }).toList();
                    
                    return ResponseEntity.ok(filesInfo);
                }
            }

            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
