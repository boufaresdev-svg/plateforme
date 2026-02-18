package com.example.BacK.presentation.g_RH;

import com.example.BacK.application.g_RH.Command.fichePaie.addFichePaie.AddFichePaieCommand;
import com.example.BacK.application.g_RH.Command.fichePaie.addFichePaie.AddFichePaieResponse;
import com.example.BacK.application.g_RH.Command.fichePaie.deleteFichePaie.DeleteFichePaieCommand;
import com.example.BacK.application.g_RH.Command.fichePaie.updateFichePaie.UpdateFichePaieCommand;
import com.example.BacK.application.g_RH.Query.fichePaie.GetFichePaieQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/FichePaie")
public class FichePaieController {

    private final Mediator mediator;

    public FichePaieController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter une fiche de paie",
            description = "Crée une nouvelle fiche de paie appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('FICHEPAIE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddFichePaieResponse>> add(@RequestBody AddFichePaieCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour une fiche de paie",
            description = "Met à jour une fiche de paie existante appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('FICHEPAIE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateFichePaieCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer une fiche de paie",
            description = "Supprime une fiche de paie appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('FICHEPAIE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteFichePaieCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les fiches de paie",
            description = "Recherche les fiches de paie appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('FICHEPAIE_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetFichePaieQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer toutes les fiches de paie",
            description = "Retourne la liste de toutes les fiches de paie appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('FICHEPAIE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetFichePaieQuery query = new GetFichePaieQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
