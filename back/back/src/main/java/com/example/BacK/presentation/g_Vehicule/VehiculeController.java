package com.example.BacK.presentation.g_Vehicule;

import com.example.BacK.application.mediator.Mediator;
 import com.example.BacK.application.g_Vehicule.Command.vehicule.UpdateVehicule.UpdateVehiculeCommand;
import com.example.BacK.application.g_Vehicule.Command.vehicule.addVehicule.AddVehiculeCommand;
import com.example.BacK.application.g_Vehicule.Command.vehicule.addVehicule.AddVehiculeResponse;
import com.example.BacK.application.g_Vehicule.Command.vehicule.deleteVehicule.DeleteVehiculeCommand;
import com.example.BacK.application.g_Vehicule.Query.vehicule.GetVehiculeQuery;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/vehicule")
public class VehiculeController {

    private final Mediator mediator;

    public VehiculeController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter un véhicule", description = "Crée un nouveau véhicule dans le système")
    @PreAuthorize("hasAuthority('VEHICULE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddVehiculeResponse>> add(@RequestBody AddVehiculeCommand command) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour un véhicule", description = "Met à jour les informations d'un véhicule existant")
    @PreAuthorize("hasAuthority('VEHICULE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateVehiculeCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer un véhicule", description = "Supprime un véhicule existant par son ID")
    @PreAuthorize("hasAuthority('VEHICULE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteVehiculeCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrer les véhicules", description = "Recherche les véhicules selon des critères spécifiques")
    @PreAuthorize("hasAuthority('VEHICULE_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetVehiculeQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Lire tous les véhicules", description = "Récupère la liste de tous les véhicules")
    @PreAuthorize("hasAuthority('VEHICULE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetVehiculeQuery query = new GetVehiculeQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

}