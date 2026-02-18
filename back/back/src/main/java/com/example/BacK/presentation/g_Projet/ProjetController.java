package com.example.BacK.presentation.g_Projet;

import com.example.BacK.application.g_Projet.Command.projet.add.AddProjetCommand;
import com.example.BacK.application.g_Projet.Command.projet.add.AddProjetResponse;
import com.example.BacK.application.g_Projet.Command.projet.delete.DeleteProjetCommand;
import com.example.BacK.application.g_Projet.Command.projet.update.UpdateProjetCommand;
import com.example.BacK.application.g_Projet.Query.projet.all.GetProjetQuery;
import com.example.BacK.application.g_Projet.Query.projet.all.GetProjetResponse;
import com.example.BacK.application.g_Projet.Query.projet.byId.GetProjetByIDHandler;
import com.example.BacK.application.g_Projet.Query.projet.byId.GetProjetByIDQuery;
import com.example.BacK.application.g_Projet.Query.projet.byId.GetProjetByIDResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/projets")
public class ProjetController {

    private final Mediator mediator;

    public ProjetController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter un projet",
            description = "Crée un nouveau projet appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROJET_CREER')")
    @PostMapping
    public ResponseEntity<List<AddProjetResponse>> add(@RequestBody AddProjetCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour un projet",
            description = "Met à jour un projet existant appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROJET_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateProjetCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un projet",
            description = "Supprime un projet appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROJET_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteProjetCommand(id));
        return ResponseEntity.noContent().build();
    }
/*
    @Operation(
            summary = "Filtrer les projets",
            description = "Recherche les projets appliqués en interne selon le protocole de travail de la société"
    )
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetProjetQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
*/
    @Operation(
            summary = "Récupérer tous les projets",
            description = "Retourne la liste de tous les projets appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PROJET_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetProjetQuery query = new GetProjetQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Récupérer un projet par ID")
    @PreAuthorize("hasAuthority('PROJET_LIRE')")
    @GetMapping("/projet/{id}")
    public ResponseEntity<List<Object>> getById(@PathVariable String id) {
        GetProjetByIDQuery query = new GetProjetByIDQuery(id);
       return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}