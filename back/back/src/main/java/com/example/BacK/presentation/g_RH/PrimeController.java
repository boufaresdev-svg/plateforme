package com.example.BacK.presentation.g_RH;

import com.example.BacK.application.g_RH.Command.prime.add.AddPrimeCommand;
import com.example.BacK.application.g_RH.Command.prime.add.AddPrimeResponse;
import com.example.BacK.application.g_RH.Command.prime.delete.DeletePrimeCommand;
import com.example.BacK.application.g_RH.Command.prime.update.UpdatePrimeCommand;
import com.example.BacK.application.g_RH.Query.prime.GetPrimeQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/Prime")
public class PrimeController {

    private final Mediator mediator;

    public PrimeController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Ajouter une prime",
            description = "Crée une nouvelle prime appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PRIME_LIRE')")
    @PostMapping
    public ResponseEntity<List<AddPrimeResponse>> add(@RequestBody AddPrimeCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(
            summary = "Mettre à jour une prime",
            description = "Met à jour une prime existante appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PRIME_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdatePrimeCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer une prime",
            description = "Supprime une prime appliquée en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PRIME_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeletePrimeCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrer les primes",
            description = "Recherche les primes appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PRIME_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetPrimeQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(
            summary = "Récupérer toutes les primes",
            description = "Retourne la liste de toutes les primes appliquées en interne selon le protocole de travail de la société"
    )
    @PreAuthorize("hasAuthority('PRIME_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetPrimeQuery query = new GetPrimeQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
