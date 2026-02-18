package com.example.BacK.presentation.g_Projet;

import com.example.BacK.application.g_Projet.Command.ProblemeTache.add.AddProblemeTacheCommand;
import com.example.BacK.application.g_Projet.Command.ProblemeTache.add.AddProblemeTacheResponse;
import com.example.BacK.application.g_Projet.Command.ProblemeTache.delete.DeleteProblemeTacheCommand;
import com.example.BacK.application.g_Projet.Command.ProblemeTache.update.UpdateProblemeTacheCommand;
import com.example.BacK.application.g_Projet.Query.ProblemeTache.GetProblemeTacheQuery;
import com.example.BacK.application.g_Projet.Query.ProblemeTache.GetProblemeTacheResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/problemeTaches")
public class ProblemeTacheController {

    private final Mediator mediator;

    public ProblemeTacheController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter un problème de tâche",
            description = "Crée un nouveau problème appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROBLEMETACHE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddProblemeTacheResponse>> add(@RequestBody AddProblemeTacheCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour un problème de tâche",
            description = "Met à jour un problème existant appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROBLEMETACHE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateProblemeTacheCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un problème de tâche",
            description = "Supprime un problème appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROBLEMETACHE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteProblemeTacheCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les problèmes de tâches",
            description = "Recherche les problèmes appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROBLEMETACHE_CREER')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetProblemeTacheQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer tous les problèmes de tâches",
            description = "Retourne la liste de tous les problèmes appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROBLEMETACHE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetProblemeTacheQuery query = new GetProblemeTacheQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
/*
    @Operation(
            summary = "Récupérer un problème de tâche par ID",
            description = "Retourne un problème spécifique appliqué en interne selon le protocole de travail de la société"
    )
  /*  @PreAuthorize("hasAuthority('PROBLEMETACHE_LIRE')")
    @GetMapping("/{id}")
    public ResponseEntity<GetProblemeTacheResponse> getById(@PathVariable String id) {
        GetProblemeTacheByIdQuery query = new GetProblemeTacheByIdQuery(id);
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }*/
}
