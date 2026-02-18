package com.example.BacK.presentation.g_Vehicule;

import com.example.BacK.application.g_Vehicule.Command.prix_Carburant.UpdatePrixCarburantCommand;
import com.example.BacK.application.mediator.Mediator;
import com.example.BacK.domain.g_Vehicule.Prix_Carburant;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/prix-carburant")
public class Prix_Carburant_Controller {

    private final Mediator mediator;

    public Prix_Carburant_Controller(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Mettre à jour le prix carburant", description = "Met à jour le prix d'un carburant par son ID")
    @PreAuthorize("hasAuthority('PRIXCARBURANT_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Prix_Carburant> update(@PathVariable String id,   @RequestBody UpdatePrixCarburantCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Récupérer tous les prix carburant", description = "Retourne la liste de tous les prix de carburant")
    @PreAuthorize("hasAuthority('PRIXCARBURANT_LIRE')")
    @GetMapping
    public ResponseEntity<List<Prix_Carburant>> getAll() {
        Prix_Carburant query = new Prix_Carburant();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}

