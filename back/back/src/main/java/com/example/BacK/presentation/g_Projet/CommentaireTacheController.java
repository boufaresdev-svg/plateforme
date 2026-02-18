package com.example.BacK.presentation.g_Projet;


import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheCommand;
import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheResponse;
import com.example.BacK.application.g_Projet.Command.commentaireTache.delete.DeleteCommentaireTacheCommand;
import com.example.BacK.application.g_Projet.Command.commentaireTache.update.UpdateCommentaireTacheCommand;
import com.example.BacK.application.g_Projet.Query.commentaireTache.GetCommentaireTacheQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/commentaireTaches")
public class CommentaireTacheController {

    private final Mediator mediator;

    public CommentaireTacheController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter un commentaire de tâche",
            description = "Crée un nouveau commentaire appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('COMMENTAIRETACHE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddCommentaireTacheResponse>> add(@RequestBody AddCommentaireTacheCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour un commentaire de tâche",
            description = "Met à jour un commentaire existant appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('COMMENTAIRETACHE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateCommentaireTacheCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un commentaire de tâche",
            description = "Supprime un commentaire appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('COMMENTAIRETACHE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteCommentaireTacheCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les commentaires de tâches",
            description = "Recherche les commentaires appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('COMMENTAIRETACHE_CREER')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetCommentaireTacheQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer tous les commentaires de tâches",
            description = "Retourne la liste de tous les commentaires appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('COMMENTAIRETACHE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetCommentaireTacheQuery query = new GetCommentaireTacheQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
