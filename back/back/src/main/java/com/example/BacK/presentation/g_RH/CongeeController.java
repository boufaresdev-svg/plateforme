package com.example.BacK.presentation.g_RH;

import com.example.BacK.application.g_RH.Command.congee.addCongee.AddCongeeCommand;
import com.example.BacK.application.g_RH.Command.congee.addCongee.AddCongeeResponse;
import com.example.BacK.application.g_RH.Command.congee.deleteCongee.DeleteCongeeCommand;
import com.example.BacK.application.g_RH.Command.congee.updateCongee.UpdateCongeeCommand;
import com.example.BacK.application.g_RH.Query.congee.GetCongeeQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Congee")
public class CongeeController {

    private final Mediator mediator;

    public CongeeController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter un congé",
            description = "Crée un nouveau congé appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CONGEE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddCongeeResponse>> add(@RequestBody AddCongeeCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour un congé",
            description = "Met à jour un congé existant appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CONGEE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateCongeeCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un congé",
            description = "Supprime un congé appliqué en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CONGEE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteCongeeCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les congés",
            description = "Recherche les congés appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CONGEE_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetCongeeQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer tous les congés",
            description = "Retourne la liste de tous les congés appliqués en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CONGEE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetCongeeQuery query = new GetCongeeQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}