package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Query.stockLot.GetStockLotQuery;
import com.example.BacK.application.g_Stock.Query.stockLot.GetStockLotResponse;
import com.example.BacK.application.g_Stock.Query.stockLotMouvement.GetStockLotMouvementQuery;
import com.example.BacK.application.g_Stock.Query.stockLotMouvement.GetStockLotMouvementResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/lots")
@RequiredArgsConstructor
@Tag(name = "Stock Lots (Batches)", description = "API de gestion des lots de stock avec traçabilité complète")
@SecurityRequirement(name = "bearerAuth")
public class StockLotController {

    private final Mediator mediator;
    
    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;
    
    @Value("${app.upload.base-url:/api/stock/lots/uploads}")
    private String uploadBaseUrl;

    @PostMapping("/upload-invoice")
    @Operation(summary = "Upload invoice file", description = "Upload a PDF, JPG or PNG invoice file")
    public ResponseEntity<Map<String, String>> uploadInvoice(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est vide"));
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("application/pdf") 
                && !contentType.equals("image/jpeg") 
                && !contentType.equals("image/png") 
                && !contentType.equals("image/jpg"))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Type de fichier non autorisé. Utilisez PDF, JPG ou PNG"));
            }

            // Check file size (5MB max)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est trop volumineux (max 5MB)"));
            }

            // Create upload directory for invoices if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, "invoices");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return URL using configured base URL with invoices subdirectory
            String fileUrl = uploadBaseUrl + "/invoices/" + uniqueFilename;
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("filename", uniqueFilename);
            response.put("originalFilename", originalFilename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de l'enregistrement du fichier: " + e.getMessage()));
        }
    }

    @GetMapping("/uploads/{subdir}/{filename}")
    @Operation(summary = "Download uploaded file", description = "Download an uploaded file by subdirectory and filename")
    public ResponseEntity<Resource> downloadFile(@PathVariable String subdir, @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir, subdir, filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/invoices/{filename}")
    @Operation(summary = "Download invoice file (legacy)", description = "Download an invoice file by filename")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable String filename) {
        return downloadFile("invoices", filename);
    }

    @GetMapping
    @Operation(summary = "Lister tous les lots de stock", description = "Récupérer la liste de tous les lots avec filtres optionnels")
    public ResponseEntity<List<GetStockLotResponse>> getAllStockLots(
            @RequestParam(required = false) String articleId,
            @RequestParam(required = false) String entrepotId,
            @RequestParam(required = false) Boolean estActif,
            @RequestParam(required = false) Boolean availableOnly) {
        
        GetStockLotQuery query = new GetStockLotQuery();
        query.setArticleId(articleId);
        query.setEntrepotId(entrepotId);
        query.setEstActif(estActif);
        query.setAvailableOnly(availableOnly != null && availableOnly);
        
        var result = mediator.sendToHandlers(query);
        if (!result.isEmpty() && result.get(0) instanceof List) {
            @SuppressWarnings("unchecked")
            List<GetStockLotResponse> lots = (List<GetStockLotResponse>) result.get(0);
            return ResponseEntity.ok(lots);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un lot par ID", description = "Récupérer les détails d'un lot spécifique")
    public ResponseEntity<GetStockLotResponse> getStockLotById(@PathVariable String id) {
        GetStockLotQuery query = new GetStockLotQuery();
        query.setId(id);
        
        var result = mediator.sendToHandlers(query);
        if (!result.isEmpty() && result.get(0) instanceof GetStockLotResponse) {
            return ResponseEntity.ok((GetStockLotResponse) result.get(0));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Obtenir les lots par article", description = "Récupérer tous les lots d'un article spécifique")
    public ResponseEntity<List<GetStockLotResponse>> getStockLotsByArticle(@PathVariable String articleId) {
        GetStockLotQuery query = new GetStockLotQuery();
        query.setArticleId(articleId);
        
        var result = mediator.sendToHandlers(query);
        if (!result.isEmpty() && result.get(0) instanceof List) {
            @SuppressWarnings("unchecked")
            List<GetStockLotResponse> lots = (List<GetStockLotResponse>) result.get(0);
            return ResponseEntity.ok(lots);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/article/{articleId}/entrepot/{entrepotId}")
    @Operation(summary = "Obtenir les lots par article et entrepôt", description = "Récupérer tous les lots d'un article dans un entrepôt spécifique")
    public ResponseEntity<List<GetStockLotResponse>> getStockLotsByArticleAndEntrepot(
            @PathVariable String articleId, 
            @PathVariable String entrepotId) {
        
        GetStockLotQuery query = new GetStockLotQuery();
        query.setArticleId(articleId);
        query.setEntrepotId(entrepotId);
        
        var result = mediator.sendToHandlers(query);
        if (!result.isEmpty() && result.get(0) instanceof List) {
            @SuppressWarnings("unchecked")
            List<GetStockLotResponse> lots = (List<GetStockLotResponse>) result.get(0);
            return ResponseEntity.ok(lots);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/expiring")
    @Operation(summary = "Obtenir les lots qui expirent", description = "Récupérer les lots qui expirent avant une date donnée")
    public ResponseEntity<List<GetStockLotResponse>> getExpiringLots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate) {
        
        GetStockLotQuery query = new GetStockLotQuery();
        query.setExpiringBefore(beforeDate);
        
        var result = mediator.sendToHandlers(query);
        if (!result.isEmpty() && result.get(0) instanceof List) {
            @SuppressWarnings("unchecked")
            List<GetStockLotResponse> lots = (List<GetStockLotResponse>) result.get(0);
            return ResponseEntity.ok(lots);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Obtenir l'historique d'un lot", description = "Récupérer tous les mouvements d'un lot spécifique")
    public ResponseEntity<List<GetStockLotMouvementResponse>> getStockLotHistory(@PathVariable String id) {
        GetStockLotMouvementQuery query = new GetStockLotMouvementQuery();
        query.setStockLotId(id);
        
        var result = mediator.sendToHandlers(query);
        if (!result.isEmpty() && result.get(0) instanceof List) {
            @SuppressWarnings("unchecked")
            List<GetStockLotMouvementResponse> mouvements = (List<GetStockLotMouvementResponse>) result.get(0);
            return ResponseEntity.ok(mouvements);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/history/article/{articleId}")
    @Operation(summary = "Obtenir l'historique par article", description = "Récupérer tous les mouvements de lots pour un article")
    public ResponseEntity<List<GetStockLotMouvementResponse>> getArticleHistory(@PathVariable String articleId) {
        GetStockLotMouvementQuery query = new GetStockLotMouvementQuery();
        query.setArticleId(articleId);
        
        var result = mediator.sendToHandlers(query);
        if (!result.isEmpty() && result.get(0) instanceof List) {
            @SuppressWarnings("unchecked")
            List<GetStockLotMouvementResponse> mouvements = (List<GetStockLotMouvementResponse>) result.get(0);
            return ResponseEntity.ok(mouvements);
        }
        return ResponseEntity.ok(List.of());
    }
}
