package com.example.BacK.presentation.g_Projet;


import com.example.BacK.application.g_Projet.Command.charge.add.AddChargeCommand;
import com.example.BacK.application.g_Projet.Command.charge.add.AddChargeResponse;
import com.example.BacK.application.g_Projet.Command.charge.delete.DeleteChargeCommand;
import com.example.BacK.application.g_Projet.Command.charge.update.UpdateChargeCommand;
import com.example.BacK.application.g_Projet.Query.charge.GetChargeQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/charges")
public class ChargeController {

    private final Mediator mediator;

    public ChargeController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter une charge",
            description = "Crée une nouvelle charge appliquée en interne selon le protocole de travail de la société"
    )@PreAuthorize("hasAuthority('CHARGE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddChargeResponse>> add(@RequestBody AddChargeCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour une charge",
            description = "Met à jour une charge existante appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CHARGE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateChargeCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer une charge",
            description = "Supprime une charge appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CHARGE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteChargeCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les charges",
            description = "Recherche les charges appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CHARGE_CREER')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetChargeQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer toutes les charges",
            description = "Retourne la liste de toutes les charges appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('CHARGE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetChargeQuery query = new GetChargeQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
