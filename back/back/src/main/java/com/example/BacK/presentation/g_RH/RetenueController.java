package com.example.BacK.presentation.g_RH;

import com.example.BacK.application.g_RH.Command.retenue.add.AddRetenueResponse;
import com.example.BacK.application.g_RH.Command.retenue.delete.DeleteRetenueCommand;
import com.example.BacK.application.g_RH.Command.retenue.update.UpdateRetenueCommand;
import com.example.BacK.application.g_RH.Query.retenue.GetRetenueQuery;
import com.example.BacK.application.interfaces.g_rh.retenue.IAddRetenueCommand;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Retenue")
public class RetenueController {

    private final Mediator mediator;

    public RetenueController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter une retenue", description = "Crée une nouvelle retenue")
    @PreAuthorize("hasAuthority('RETENUE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddRetenueResponse>> add(@RequestBody IAddRetenueCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour une retenue", description = "Met à jour une retenue existante")
    @PreAuthorize("hasAuthority('RETENUE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateRetenueCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer une retenue", description = "Supprime une retenue par son ID")
    @PreAuthorize("hasAuthority('RETENUE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteRetenueCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrer les retenues", description = "Recherche les retenues selon des critères")
    @PreAuthorize("hasAuthority('RETENUE_CREER')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetRetenueQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Récupérer toutes les retenues", description = "Retourne la liste de toutes les retenues")
    @PreAuthorize("hasAuthority('RETENUE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetRetenueQuery query = new GetRetenueQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
