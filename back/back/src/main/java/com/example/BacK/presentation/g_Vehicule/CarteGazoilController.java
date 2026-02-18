package com.example.BacK.presentation.g_Vehicule;

import com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil.AddCarteGazoilCommand;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil.AddCarteGazoilResponse;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.deleteGazoil.DeleteCarteGazoilCommand;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.updateGazoil.UpdateCarteGazoilCommand;
import com.example.BacK.application.g_Vehicule.Query.CarteGazoil.GetCarteGazoilQuery;
import com.example.BacK.application.mediator.Mediator;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/carte-gazoil")
public class CarteGazoilController {

    private final Mediator mediator;

    public CarteGazoilController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter une carte gazoil", description = "Crée une nouvelle carte gazoil")
    @PreAuthorize("hasAuthority('CARTEGAZOIL_CREER')")
    @PostMapping
    public ResponseEntity<List<AddCarteGazoilResponse>> add(@RequestBody AddCarteGazoilCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour une carte gazoil", description = "Met à jour une carte gazoil existante")
    @PreAuthorize("hasAuthority('CARTEGAZOIL_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateCarteGazoilCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer une carte gazoil", description = "Supprime une carte gazoil par son ID")
    @PreAuthorize("hasAuthority('CARTEGAZOIL_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteCarteGazoilCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrer les cartes gazoil", description = "Recherche les cartes gazoil selon des critères")
    @PreAuthorize("hasAuthority('CARTEGAZOIL_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetCarteGazoilQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Récupérer toutes les cartes gazoil", description = "Retourne la liste de toutes les cartes gazoil")
    @PreAuthorize("hasAuthority('CARTEGAZOIL_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetCarteGazoilQuery query = new GetCarteGazoilQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}