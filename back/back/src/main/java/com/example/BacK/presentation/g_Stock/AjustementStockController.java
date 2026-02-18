package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Command.ajustementStock.add.AddAjustementStockCommand;
import com.example.BacK.application.g_Stock.Command.ajustementStock.add.AddAjustementStockResponse;
import com.example.BacK.application.g_Stock.Command.ajustementStock.delete.DeleteAjustementStockCommand;
import com.example.BacK.application.g_Stock.Command.ajustementStock.delete.DeleteAjustementStockResponse;
import com.example.BacK.application.g_Stock.Command.ajustementStock.update.UpdateAjustementStockCommand;
import com.example.BacK.application.g_Stock.Command.ajustementStock.update.UpdateAjustementStockResponse;
import com.example.BacK.application.g_Stock.Query.ajustementStock.GetAjustementStockQuery;
import com.example.BacK.application.g_Stock.Query.ajustementStock.GetAjustementStockResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/stock/ajustement")
@Tag(name = "Ajustement Stock", description = "API de gestion des ajustements de stock")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AjustementStockController {

    private final Mediator mediator;

    @Operation(summary = "Ajouter un ajustement de stock", description = "Crée un nouvel ajustement de stock")
    @PostMapping
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_CREER')")
    public ResponseEntity<AddAjustementStockResponse> add(@Valid @RequestBody AddAjustementStockCommand command) {
        List<AddAjustementStockResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
    }

    @Operation(summary = "Mettre à jour un ajustement de stock", description = "Met à jour un ajustement de stock existant")
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_MODIFIER')")
    public ResponseEntity<UpdateAjustementStockResponse> update(@PathVariable String id, 
                                                                @Valid @RequestBody UpdateAjustementStockCommand command) {
        command.setId(id);
        List<UpdateAjustementStockResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Supprimer un ajustement de stock", description = "Supprime un ajustement de stock par son ID")
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_SUPPRIMER')")
    public ResponseEntity<DeleteAjustementStockResponse> delete(@PathVariable String id) {
        DeleteAjustementStockCommand command = new DeleteAjustementStockCommand(id);
        List<DeleteAjustementStockResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.ok(results.get(0));
    }

    @Operation(summary = "Obtenir les ajustements de stock", description = "Récupère tous les ajustements de stock")
    @GetMapping
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_LIRE')")
    public ResponseEntity<List<GetAjustementStockResponse>> getAll() {
        GetAjustementStockQuery query = new GetAjustementStockQuery();
        List<List<GetAjustementStockResponse>> results = mediator.sendToHandlers(query);
        List<GetAjustementStockResponse> ajustements = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(ajustements);
    }

    @Operation(summary = "Obtenir un ajustement de stock par ID", description = "Récupère un ajustement de stock spécifique par son ID")
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_LIRE')")
    public ResponseEntity<GetAjustementStockResponse> getById(@PathVariable String id) {
        GetAjustementStockQuery query = new GetAjustementStockQuery();
        query.setId(id);
        List<List<GetAjustementStockResponse>> results = mediator.sendToHandlers(query);
        List<GetAjustementStockResponse> ajustements = results.isEmpty() ? List.of() : results.get(0);
        
        if (ajustements.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(ajustements.get(0));
    }

    @Operation(summary = "Obtenir les ajustements par article", description = "Récupère tous les ajustements de stock pour un article spécifique")
    @GetMapping("/article/{articleId}")
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_LIRE')")
    public ResponseEntity<List<GetAjustementStockResponse>> getByArticleId(@PathVariable String articleId) {
        GetAjustementStockQuery query = new GetAjustementStockQuery();
        query.setArticleId(articleId);
        List<List<GetAjustementStockResponse>> results = mediator.sendToHandlers(query);
        List<GetAjustementStockResponse> ajustements = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(ajustements);
    }

    @Operation(summary = "Obtenir les ajustements par utilisateur", description = "Récupère tous les ajustements de stock effectués par un utilisateur spécifique")
    @GetMapping("/utilisateur/{utilisateurId}")
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_LIRE')")
    public ResponseEntity<List<GetAjustementStockResponse>> getByUtilisateurId(@PathVariable String utilisateurId) {
        GetAjustementStockQuery query = new GetAjustementStockQuery();
        query.setUtilisateurId(utilisateurId);
        List<List<GetAjustementStockResponse>> results = mediator.sendToHandlers(query);
        List<GetAjustementStockResponse> ajustements = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(ajustements);
    }

    @Operation(summary = "Obtenir les ajustements par période", description = "Récupère tous les ajustements de stock dans une période donnée")
    @GetMapping("/periode")
    // @PreAuthorize("hasAuthority('AJUSTEMENT_STOCK_LIRE')")
    public ResponseEntity<List<GetAjustementStockResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        GetAjustementStockQuery query = new GetAjustementStockQuery();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        List<List<GetAjustementStockResponse>> results = mediator.sendToHandlers(query);
        List<GetAjustementStockResponse> ajustements = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(ajustements);
    }
}
