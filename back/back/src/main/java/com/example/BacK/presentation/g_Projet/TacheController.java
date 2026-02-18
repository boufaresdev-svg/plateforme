package com.example.BacK.presentation.g_Projet;

import com.example.BacK.application.g_Projet.Command.Tache.Delete.DeleteTacheCommand;
import com.example.BacK.application.g_Projet.Command.Tache.add.AddTacheCommand;
import com.example.BacK.application.g_Projet.Command.Tache.add.AddTacheResponse;
import com.example.BacK.application.g_Projet.Command.Tache.update.UpdateTacheCommand;
import com.example.BacK.application.g_Projet.Query.Tache.ById.GetTacheByIDQuery;
import com.example.BacK.application.g_Projet.Query.Tache.all.GetTacheQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/taches")
public class TacheController {

    private final Mediator mediator;

    public TacheController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter une tâche",
            description = "Crée une nouvelle tâche appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('TACHE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddTacheResponse>> add(@RequestBody AddTacheCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour une tâche",
            description = "Met à jour une tâche existante appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('TACHE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateTacheCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer une tâche",
            description = "Supprime une tâche appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('TACHE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteTacheCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les tâches",
            description = "Recherche les tâches appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('TACHE_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetTacheQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer toutes les tâches",
            description = "Retourne la liste de toutes les tâches appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('TACHE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetTacheQuery query = new GetTacheQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Récupérer un Tache par ID")
    @PreAuthorize("hasAuthority('TACHE_LIRE')")
    @GetMapping("/tache/{id}")
    public ResponseEntity<List<Object>> getById(@PathVariable String id) {
        GetTacheByIDQuery query = new GetTacheByIDQuery(id);
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
