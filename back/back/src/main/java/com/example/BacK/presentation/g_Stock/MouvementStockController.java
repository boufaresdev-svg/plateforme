package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Command.mouvementStock.entreeStock.EntreeStockCommand;
import com.example.BacK.application.g_Stock.Command.mouvementStock.entreeStock.EntreeStockResponse;
import com.example.BacK.application.g_Stock.Command.mouvementStock.sortieStock.SortieStockCommand;
import com.example.BacK.application.g_Stock.Command.mouvementStock.sortieStock.SortieStockResponse;
import com.example.BacK.application.g_Stock.Command.mouvementStock.transfertStock.TransfertStockCommand;
import com.example.BacK.application.g_Stock.Command.mouvementStock.transfertStock.TransfertStockResponse;
import com.example.BacK.application.g_Stock.Query.mouvementStock.GetMouvementStockQuery;
import com.example.BacK.application.g_Stock.Query.mouvementStock.GetMouvementStockResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stock/mouvement")
@Tag(name = "Mouvement Stock", description = "API de gestion des mouvements de stock")
@SecurityRequirement(name = "bearerAuth")
public class MouvementStockController {

    private final Mediator mediator;

    public MouvementStockController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Créer une entrée de stock", description = "Enregistre une entrée de stock (achat, retour client, ajustement positif)")
    @PostMapping("/entree")
    // @PreAuthorize("hasAuthority('STOCK_ENTREE_CREER')")
    public ResponseEntity<EntreeStockResponse> createEntree(@Valid @RequestBody EntreeStockCommand command) {
        List<EntreeStockResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
    }

    @Operation(summary = "Créer une sortie de stock", description = "Enregistre une sortie de stock (vente, retour fournisseur, ajustement négatif)")
    @PostMapping("/sortie")
    // @PreAuthorize("hasAuthority('STOCK_SORTIE_CREER')")
    public ResponseEntity<SortieStockResponse> createSortie(@Valid @RequestBody SortieStockCommand command) {
        List<SortieStockResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
    }

    @Operation(summary = "Créer un transfert de stock", description = "Enregistre un transfert de stock entre deux entrepôts")
    @PostMapping("/transfert")
    // @PreAuthorize("hasAuthority('STOCK_TRANSFERT_CREER')")
    public ResponseEntity<TransfertStockResponse> createTransfert(@Valid @RequestBody TransfertStockCommand command) {
        List<TransfertStockResponse> results = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(results.get(0));
    }

    @Operation(summary = "Obtenir les mouvements de stock", description = "Récupère les mouvements de stock selon les critères")
    @GetMapping
    // @PreAuthorize("hasAuthority('STOCK_MOUVEMENT_LIRE')")
    public ResponseEntity<List<GetMouvementStockResponse>> getMouvements(@Valid GetMouvementStockQuery query) {
        List<List<GetMouvementStockResponse>> results = mediator.sendToHandlers(query);
        List<GetMouvementStockResponse> mouvements = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(mouvements);
    }

    @Operation(summary = "Obtenir un mouvement par ID", description = "Récupère un mouvement de stock spécifique par son ID")
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('STOCK_MOUVEMENT_LIRE')")
    public ResponseEntity<GetMouvementStockResponse> getMouvementById(@PathVariable String id) {
        GetMouvementStockQuery query = new GetMouvementStockQuery();
        query.setId(id);
        List<List<GetMouvementStockResponse>> results = mediator.sendToHandlers(query);
        List<GetMouvementStockResponse> mouvements = results.isEmpty() ? List.of() : results.get(0);
        if (mouvements.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mouvements.get(0));
    }
}
