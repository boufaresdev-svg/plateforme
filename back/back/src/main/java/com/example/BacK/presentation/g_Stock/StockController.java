package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Command.stock.addStock.AddStockCommand;
import com.example.BacK.application.g_Stock.Command.stock.addStock.AddStockResponse;
import com.example.BacK.application.g_Stock.Command.stock.updateStock.UpdateStockCommand;
import com.example.BacK.application.g_Stock.Command.stock.updateStock.UpdateStockResponse;
import com.example.BacK.application.g_Stock.Command.stock.deleteStock.DeleteStockCommand;
import com.example.BacK.application.g_Stock.Command.stock.deleteStock.DeleteStockResponse;
import com.example.BacK.application.g_Stock.Query.stock.GetStockQuery;
import com.example.BacK.application.g_Stock.Query.stock.GetStockResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "API de gestion du stock")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final Mediator mediator;

    @PostMapping
    @Operation(summary = "Ajouter un stock", description = "Créer un nouveau stock pour un article dans un entrepôt")
    public ResponseEntity<?> addStock(@Valid @RequestBody AddStockCommand command) {
        try {
            var result = mediator.sendToHandlers(command);
            if (!result.isEmpty() && result.get(0) instanceof AddStockResponse) {
                return ResponseEntity.status(HttpStatus.CREATED).body((AddStockResponse) result.get(0));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur interne lors de la création du stock"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur inattendue: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un stock", description = "Mettre à jour les informations d'un stock existant")
    public ResponseEntity<UpdateStockResponse> updateStock(@PathVariable String id, 
                                                          @Valid @RequestBody UpdateStockCommand command) {
        try {
            command.setId(id);
            var result = mediator.sendToHandlers(command);
            if (!result.isEmpty() && result.get(0) instanceof UpdateStockResponse) {
                return ResponseEntity.ok((UpdateStockResponse) result.get(0));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un stock", description = "Supprimer un stock par son ID")
    public ResponseEntity<DeleteStockResponse> deleteStock(@PathVariable String id) {
        try {
            var command = new DeleteStockCommand(id);
            var result = mediator.sendToHandlers(command);
            if (!result.isEmpty() && result.get(0) instanceof DeleteStockResponse) {
                return ResponseEntity.ok((DeleteStockResponse) result.get(0));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un stock par ID", description = "Récupérer les détails d'un stock spécifique")
    public ResponseEntity<GetStockResponse> getStockById(@PathVariable String id) {
        try {
            var query = new GetStockQuery();
            query.setId(id);
            var result = mediator.sendToHandlers(query);
            if (!result.isEmpty() && result.get(0) instanceof GetStockResponse) {
                return ResponseEntity.ok((GetStockResponse) result.get(0));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "Lister tous les stocks", description = "Récupérer la liste de tous les stocks avec filtres optionnels")
    public ResponseEntity<List<GetStockResponse>> getAllStocks(
            @RequestParam(required = false) String articleId,
            @RequestParam(required = false) String entrepotId,
            @RequestParam(required = false) String categorieId,
            @RequestParam(required = false) String marqueId,
            @RequestParam(required = false) String fournisseurId,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        try {
            var query = new GetStockQuery();
            query.setArticleId(articleId);
            query.setEntrepotId(entrepotId);
            query.setCategorieId(categorieId);
            query.setMarqueId(marqueId);
            query.setFournisseurId(fournisseurId);
            query.setStatut(statut);
            query.setPage(page);
            query.setSize(size);
            var result = mediator.sendToHandlers(query);
            if (!result.isEmpty() && result.get(0) instanceof List) {
                @SuppressWarnings("unchecked")
                List<GetStockResponse> stockResponses = (List<GetStockResponse>) result.get(0);
                return ResponseEntity.ok(stockResponses);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Obtenir les stocks par article", description = "Récupérer tous les stocks d'un article spécifique")
    public ResponseEntity<List<GetStockResponse>> getStocksByArticleId(@PathVariable String articleId) {
        try {
            var query = new GetStockQuery(null, articleId, null, null, null, null, null, null, null);
            var result = mediator.sendToHandlers(query);
            if (!result.isEmpty() && result.get(0) instanceof List) {
                @SuppressWarnings("unchecked")
                List<GetStockResponse> stockResponses = (List<GetStockResponse>) result.get(0);
                return ResponseEntity.ok(stockResponses);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entrepot/{entrepotId}")
    @Operation(summary = "Obtenir les stocks par entrepôt", description = "Récupérer tous les stocks d'un entrepôt spécifique")
    public ResponseEntity<List<GetStockResponse>> getStocksByEntrepotId(@PathVariable String entrepotId) {
        try {
            var query = new GetStockQuery(null, null, entrepotId, null, null, null, null, null, null);
            var result = mediator.sendToHandlers(query);
            if (!result.isEmpty() && result.get(0) instanceof List) {
                @SuppressWarnings("unchecked")
                List<GetStockResponse> stockResponses = (List<GetStockResponse>) result.get(0);
                return ResponseEntity.ok(stockResponses);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/article/{articleId}/entrepot/{entrepotId}")
    @Operation(summary = "Obtenir le stock d'un article dans un entrepôt", description = "Récupérer le stock spécifique d'un article dans un entrepôt donné")
    public ResponseEntity<GetStockResponse> getStockByArticleAndEntrepot(
            @PathVariable String articleId, 
            @PathVariable String entrepotId) {
        try {
            var query = new GetStockQuery(null, articleId, entrepotId, null, null, null, null, null, null);
            var result = mediator.sendToHandlers(query);
            
            if (result != null && !result.isEmpty()) {
                // The handler returns List<GetStockResponse>, get the first element
                Object firstResult = result.get(0);
                if (firstResult instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<GetStockResponse> stockList = (List<GetStockResponse>) firstResult;
                    if (!stockList.isEmpty()) {
                        return ResponseEntity.ok(stockList.get(0));
                    }
                } else if (firstResult instanceof GetStockResponse) {
                    return ResponseEntity.ok((GetStockResponse) firstResult);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
