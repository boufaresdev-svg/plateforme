package com.example.BacK.presentation.g_Stock;

import com.example.BacK.application.g_Stock.Command.Article.*;
import com.example.BacK.application.g_Stock.Query.Article.GetArticleQuery;
import com.example.BacK.application.g_Stock.Query.Article.GetArticleQueryResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stock/article")
@RequiredArgsConstructor
@Tag(name = "Article", description = "API de gestion des articles")
@SecurityRequirement(name = "bearerAuth")
public class ArticleController {
    
    private final Mediator mediator;
    
    @PostMapping
    @Operation(summary = "Ajouter un article", description = "Créer un nouvel article dans le stock")
    public ResponseEntity<AddArticleCommandResponse> addArticle(@Valid @RequestBody AddArticleCommand command) {
        try {
            List<Object> response = mediator.sendToHandlers(command);
            if (!response.isEmpty() && response.get(0) instanceof AddArticleCommandResponse) {
                return ResponseEntity.status(HttpStatus.CREATED).body((AddArticleCommandResponse) response.get(0));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un article", description = "Mettre à jour les informations d'un article existant")
    public ResponseEntity<UpdateArticleCommandResponse> updateArticle(
            @PathVariable String id,
            @Valid @RequestBody UpdateArticleCommand command) {
        try {
            command.setId(id);
            List<Object> response = mediator.sendToHandlers(command);
            if (!response.isEmpty() && response.get(0) instanceof UpdateArticleCommandResponse) {
                return ResponseEntity.ok((UpdateArticleCommandResponse) response.get(0));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un article", description = "Supprimer un article par son ID")
    public ResponseEntity<DeleteArticleCommandResponse> deleteArticle(@PathVariable String id) {
        try {
            DeleteArticleCommand command = new DeleteArticleCommand(id);
            List<Object> response = mediator.sendToHandlers(command);
            if (!response.isEmpty() && response.get(0) instanceof DeleteArticleCommandResponse) {
                return ResponseEntity.ok((DeleteArticleCommandResponse) response.get(0));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Lister les articles", description = "Récupérer la liste des articles avec filtres optionnels")
    public ResponseEntity<GetArticleQueryResponse> getArticles(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String marque,
            @RequestParam(required = false) Boolean estActif,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            GetArticleQuery query = new GetArticleQuery(null, searchTerm, categorie, marque, estActif, page, size);
            List<Object> response = mediator.sendToHandlers(query);
            if (!response.isEmpty() && response.get(0) instanceof GetArticleQueryResponse) {
                return ResponseEntity.ok((GetArticleQueryResponse) response.get(0));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un article par ID", description = "Récupérer les détails d'un article spécifique")
    public ResponseEntity<GetArticleQueryResponse> getArticleById(@PathVariable String id) {
        try {
            GetArticleQuery query = new GetArticleQuery(id, null, null, null, null, 0, 1);
            List<Object> response = mediator.sendToHandlers(query);
            if (!response.isEmpty() && response.get(0) instanceof GetArticleQueryResponse) {
                return ResponseEntity.ok((GetArticleQueryResponse) response.get(0));
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
