package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Query.tracabilite.*;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stock/tracabilite")
@Tag(name = "Traçabilité Stock", description = "API de traçabilité et historique des mouvements de stock")
@SecurityRequirement(name = "bearerAuth")
public class TracabiliteController {

    private final Mediator mediator;

    public TracabiliteController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Obtenir l'historique des mouvements", description = "Récupère l'historique complet des mouvements de stock selon les critères de filtrage")
    @GetMapping
    public ResponseEntity<List<GetTracabiliteResponse>> getTracabilite(@Valid GetTracabiliteQuery query) {
        List<List<GetTracabiliteResponse>> results = mediator.sendToHandlers(query);
        List<GetTracabiliteResponse> tracabilite = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(tracabilite);
    }

    @Operation(summary = "Obtenir un mouvement par ID", description = "Récupère un mouvement de stock spécifique par son ID pour la traçabilité")
    @GetMapping("/{id}")
    public ResponseEntity<GetTracabiliteResponse> getTracabiliteById(@PathVariable String id) {
        GetTracabiliteQuery query = new GetTracabiliteQuery();
        query.setId(id);
        List<List<GetTracabiliteResponse>> results = mediator.sendToHandlers(query);
        List<GetTracabiliteResponse> tracabilite = results.isEmpty() ? List.of() : results.get(0);
        
        if (tracabilite.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tracabilite.get(0));
    }

    @Operation(summary = "Obtenir l'historique par article", description = "Récupère tous les mouvements d'un article spécifique")
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<GetTracabiliteResponse>> getTracabiliteByArticle(@PathVariable String articleId) {
        GetTracabiliteQuery query = new GetTracabiliteQuery();
        query.setArticleId(articleId);
        List<List<GetTracabiliteResponse>> results = mediator.sendToHandlers(query);
        List<GetTracabiliteResponse> tracabilite = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(tracabilite);
    }

    @Operation(summary = "Obtenir l'historique par entrepôt", description = "Récupère tous les mouvements d'un entrepôt spécifique")
    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<List<GetTracabiliteResponse>> getTracabiliteByEntrepot(@PathVariable String entrepotId) {
        GetTracabiliteQuery query = new GetTracabiliteQuery();
        query.setEntrepotId(entrepotId);
        List<List<GetTracabiliteResponse>> results = mediator.sendToHandlers(query);
        List<GetTracabiliteResponse> tracabilite = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(tracabilite);
    }

    @Operation(summary = "Obtenir l'historique par utilisateur", description = "Récupère tous les mouvements effectués par un utilisateur spécifique")
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<GetTracabiliteResponse>> getTracabiliteByUtilisateur(@PathVariable String utilisateurId) {
        GetTracabiliteQuery query = new GetTracabiliteQuery();
        query.setUtilisateurId(utilisateurId);
        List<List<GetTracabiliteResponse>> results = mediator.sendToHandlers(query);
        List<GetTracabiliteResponse> tracabilite = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(tracabilite);
    }

    @Operation(summary = "Obtenir les statistiques de traçabilité", description = "Récupère les statistiques détaillées des mouvements de stock")
    @GetMapping("/statistics")
    public ResponseEntity<GetTracabiliteStatsResponse> getStatistics(@Valid GetTracabiliteStatsQuery query) {
        List<GetTracabiliteStatsResponse> results = mediator.sendToHandlers(query);
        
        if (results.isEmpty()) {
            return ResponseEntity.ok(new GetTracabiliteStatsResponse());
        }
        return ResponseEntity.ok(results.get(0));
    }
}
