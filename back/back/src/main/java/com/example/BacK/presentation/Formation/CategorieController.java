package com.example.BacK.presentation.Formation;


import com.example.BacK.application.g_Formation.Command.Categorie.addCategorie.AddCategorieCommand;
import com.example.BacK.application.g_Formation.Command.Categorie.deleteCategorie.DeleteCategorieCommand;
import com.example.BacK.application.g_Formation.Command.Categorie.updateCategorie.UpdateCategorieCommand;
import com.example.BacK.application.g_Formation.Query.Categorie.CategoriesByType.GetCategoriesByTypeQuery;
import com.example.BacK.application.g_Formation.Query.Categorie.GetCategorieQuery;
import com.example.BacK.application.g_Formation.Query.Categorie.GetCategorieResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categorie", description = "Gestion des catégories de formation")
public class CategorieController {

    private final Mediator mediator;

    public CategorieController(Mediator mediator) {
        this.mediator = mediator;
    }


    @Operation(summary = "Obtenir la liste des catégories", description = "Retourne la liste complète des catégories disponibles")
    @GetMapping
    public ResponseEntity<List<GetCategorieResponse>> getAllCategories() {
        List<List<GetCategorieResponse>> results = mediator.sendToHandlers(new GetCategorieQuery(null));
        List<GetCategorieResponse> responses = results.isEmpty() ? List.of() : results.get(0);
        return ResponseEntity.ok(responses);
    }


    @Operation(summary = "Obtenir une catégorie par ID", description = "Retourne les informations d'une catégorie spécifique")
    @GetMapping("/{id}")
    public ResponseEntity<GetCategorieResponse> getCategorieById(@PathVariable Long id) {
        List<List<GetCategorieResponse>> results = mediator.sendToHandlers(new GetCategorieQuery(id));
        List<GetCategorieResponse> responses = results.isEmpty() ? List.of() : results.get(0);
        if (responses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responses.get(0));
    }

    @Operation(summary = "Créer une nouvelle catégorie", description = "Ajoute une nouvelle catégorie avec son type associé")
    @PostMapping
    public ResponseEntity<Object> addCategorie(@Valid @RequestBody AddCategorieCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Mettre à jour une catégorie", description = "Modifie les informations d'une catégorie existante")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategorie(@PathVariable Long id,
                                                @Valid @RequestBody UpdateCategorieCommand command) {
        command.setIdCategorie(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }



@Operation(summary = "Supprimer une catégorie", description = "Supprime une catégorie existante de la base de données")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteCategorieCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtenir les catégories par type",
            description = "Retourne la liste des catégories associées à un type de formation"
    )
    @GetMapping("/by-type/{idType}")
    public ResponseEntity<List<GetCategorieResponse>> getCategoriesByType(@PathVariable Long idType) {

        List<GetCategorieResponse> responses =
                mediator.sendToHandlers(new GetCategoriesByTypeQuery(idType));

        return ResponseEntity.ok(responses);
    }



}




