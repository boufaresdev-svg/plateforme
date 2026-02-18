package com.example.BacK.presentation.g_Vehicule;


import com.example.BacK.application.g_Vehicule.Command.carteTelepeage.addCarteTelepeage.AddCarteTelepeageCommand;
import com.example.BacK.application.g_Vehicule.Command.carteTelepeage.addCarteTelepeage.AddCarteTelepeageResponse;
import com.example.BacK.application.g_Vehicule.Command.carteTelepeage.deleteCarteTelepeage.DeleteCarteTelepeageCommand;
import com.example.BacK.application.g_Vehicule.Command.carteTelepeage.updateCarteTelepeage.UpdateCarteTelepeageCommand;
import com.example.BacK.application.g_Vehicule.Query.CarteTelepeage.GetCarteTelepeageQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/carte-telepeage")
public class CarteTelepeageController {

    private final Mediator mediator;

    public CarteTelepeageController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter une carte télépéage", description = "Crée une nouvelle carte télépéage")
    @PreAuthorize("hasAuthority('CARTETELEPEAGE_CREER')")
    @PostMapping
    public ResponseEntity<List<AddCarteTelepeageResponse>> add(@RequestBody AddCarteTelepeageCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour une carte télépéage", description = "Met à jour une carte télépéage existante")
    @PreAuthorize("hasAuthority('CARTETELEPEAGE_MODIFIER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody UpdateCarteTelepeageCommand command) {
        command.setId(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer une carte télépéage", description = "Supprime une carte télépéage par son ID")
    @PreAuthorize("hasAuthority('CARTETELEPEAGE_SUPPRIMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediator.sendToHandlers(new DeleteCarteTelepeageCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrer les cartes télépéage", description = "Recherche les cartes selon des critères")
    @PreAuthorize("hasAuthority('CARTETELEPEAGE_LIRE')")
    @PostMapping("/search")
    public ResponseEntity<List<Object>> filter(@RequestBody GetCarteTelepeageQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Récupérer toutes les cartes télépéage", description = "Retourne la liste de toutes les cartes télépéage")
    @PreAuthorize("hasAuthority('CARTETELEPEAGE_LIRE')")
    @GetMapping
    public ResponseEntity<List<Object>> getAll() {
        GetCarteTelepeageQuery query = new GetCarteTelepeageQuery();
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}
