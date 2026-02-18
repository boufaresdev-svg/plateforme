package com.example.BacK.presentation.g_Projet;

import com.example.BacK.application.g_Projet.Command.LivrableTache.add.AddLivrableTacheCommand;
import com.example.BacK.application.g_Projet.Command.LivrableTache.add.AddLivrableTacheResponse;
import com.example.BacK.application.g_Projet.Command.LivrableTache.delete.DeleteLivrableTacheCommand;
import com.example.BacK.application.g_Projet.Command.LivrableTache.update.UpdateLivrableTacheCommand;
import com.example.BacK.application.g_Projet.Query.LivrableTache.GetLivrableTacheQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/livrableTaches")
public class LivrableTacheController {

    private final Mediator mediator;

    public LivrableTacheController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter un livrable de tâche",
            description = "Crée un nouveau livrable appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('LIVRABLETACHE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddLivrableTacheResponse>> add(@RequestBody AddLivrableTacheCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour un livrable de tâche",
            description = "Met à jour un livrable existant appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('LIVRABLETACHE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateLivrableTacheCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un livrable de tâche",
            description = "Supprime un livrable appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('LIVRABLETACHE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteLivrableTacheCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les livrables de tâches",
            description = "Recherche les livrables appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('LIVRABLETACHE_CREER')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetLivrableTacheQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer tous les livrables de tâches",
            description = "Retourne la liste de tous les livrables appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('LIVRABLETACHE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetLivrableTacheQuery query = new GetLivrableTacheQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}

