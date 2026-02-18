package com.example.BacK.presentation.g_Fournisseur;

import com.example.BacK.application.g_Fournisseur.Command.fournisseur.addFournisseur.AddFournisseurCommand;
import com.example.BacK.application.g_Fournisseur.Command.fournisseur.addFournisseur.AddFournisseurResponse;
import com.example.BacK.application.g_Fournisseur.Command.fournisseur.updateFournisseur.UpdateFournisseurCommand;
import com.example.BacK.application.g_Fournisseur.Command.fournisseur.updateFournisseur.UpdateFournisseurResponse;
import com.example.BacK.application.g_Fournisseur.Command.fournisseur.deleteFournisseur.DeleteFournisseurCommand;
import com.example.BacK.application.g_Fournisseur.Command.fournisseur.deleteFournisseur.DeleteFournisseurResponse;
import com.example.BacK.application.g_Fournisseur.Query.fournisseur.GetFournisseurQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/fournisseur")
@Tag(name = "Fournisseur", description = "API de gestion des fournisseurs")
@SecurityRequirement(name = "bearerAuth")
public class FournisseurController {

    private final Mediator mediator;

    public FournisseurController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter un fournisseur", description = "Crée un nouveau fournisseur")
    @PostMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_CREER')")
    public ResponseEntity<List<AddFournisseurResponse>> add(@Valid @RequestBody AddFournisseurCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour un fournisseur", description = "Met à jour un fournisseur existant")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_MODIFIER')")
    public ResponseEntity<List<UpdateFournisseurResponse>> update(@PathVariable String id, 
                                                                  @Valid @RequestBody UpdateFournisseurCommand command) {
        command.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Supprimer un fournisseur", description = "Supprime un fournisseur par son ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_SUPPRIMER')")
    public ResponseEntity<List<DeleteFournisseurResponse>> delete(@PathVariable String id) {
        return ResponseEntity.ok(mediator.sendToHandlers(new DeleteFournisseurCommand(id)));
    }

    @Operation(summary = "Récupérer un fournisseur par ID", description = "Retourne un fournisseur spécifique")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_LIRE')")
    public ResponseEntity<List<Object>> getById(@PathVariable String id) {
        GetFournisseurQuery query = new GetFournisseurQuery();
        query.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Rechercher et filtrer les fournisseurs", 
               description = "Recherche unifiée pour tous les fournisseurs avec support de filtres multiples: " +
                           "par ID, nom, matricule fiscale, etc. " +
                           "Support de pagination: page (défaut: 0), size (défaut: 10), sortBy (défaut: nom), sortDirection (ASC/DESC)")
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('FOURNISSEUR_LIRE')")
    public ResponseEntity<List<Object>> search(@RequestBody GetFournisseurQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
}