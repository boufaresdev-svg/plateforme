package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.SousCategorie.addSousCategorie.AddSousCategorieCommand;
import com.example.BacK.application.g_Formation.Command.SousCategorie.deleteSousCategorie.DeleteSousCategorieCommand;
import com.example.BacK.application.g_Formation.Command.SousCategorie.UpdateSousCategorie.UpdateSousCategorieCommand;
import com.example.BacK.application.g_Formation.Query.SousCategorie.GetSousCategorieQuery;
import com.example.BacK.application.g_Formation.Query.SousCategorie.GetSousCategorieResponse;
import com.example.BacK.application.g_Formation.Query.SousCategorie.SousCategoriesByCategorie.GetSousCategoriesByCategorieQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/souscategories")
@Tag(name = "SousCatégorie", description = "Gestion des sous-catégories (création, consultation, mise à jour, suppression)")
public class SousCategorieController {

    private final Mediator mediator;

    public SousCategorieController(Mediator mediator) {
        this.mediator = mediator;
    }


    @Operation(summary = "Obtenir toutes les sous-catégories", description = "Retourne la liste complète des sous-catégories existantes.")
    @GetMapping
    public ResponseEntity<List<GetSousCategorieResponse>> getAllSousCategories() {
        List<GetSousCategorieResponse> result = mediator.sendToHandlers(new GetSousCategorieQuery());
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Obtenir une sous-catégorie par ID", description = "Retourne les détails d'une sous-catégorie spécifique.")
    @GetMapping("/{id}")
    public ResponseEntity<GetSousCategorieResponse> getSousCategorieById(@PathVariable Long id) {
        List<GetSousCategorieResponse> responses = mediator.sendToHandlers(new GetSousCategorieQuery(id));
        if (responses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responses.get(0));
    }

    @Operation(summary = "Créer une nouvelle sous-catégorie", description = "Ajoute une sous-catégorie liée à une catégorie existante.")
    @PostMapping
    public ResponseEntity<Object> addSousCategorie(@Valid @RequestBody AddSousCategorieCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Mettre à jour une sous-catégorie", description = "Modifie une sous-catégorie existante.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSousCategorie(@PathVariable Long id, @Valid @RequestBody UpdateSousCategorieCommand command) {
        command.setIdSousCategorie(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Supprimer une sous-catégorie", description = "Supprime une sous-catégorie existante de la base de données.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSousCategorie(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteSousCategorieCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtenir les sous-catégories par catégorie",
            description = "Retourne la liste des sous-catégories associées à une catégorie donnée."
    )
    @GetMapping("/by-categorie/{idCategorie}")
    public ResponseEntity<List<GetSousCategorieResponse>> getSousCategoriesByCategorie(
            @PathVariable Long idCategorie) {

        List<GetSousCategorieResponse> responses =
                mediator.sendToHandlers(new GetSousCategoriesByCategorieQuery(idCategorie));

        return ResponseEntity.ok(responses);
    }



}
