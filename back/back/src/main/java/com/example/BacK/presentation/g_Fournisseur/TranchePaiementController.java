package com.example.BacK.presentation.g_Fournisseur;

import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.addTranchePaiement.AddTranchePaiementCommand;
import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.addTranchePaiement.AddTranchePaiementResponse;
import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.payTranchePaiement.PayTranchePaiementCommand;
import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.payTranchePaiement.PayTranchePaiementResponse;
import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.updateTranchePaiement.UpdateTranchePaiementCommand;
import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.updateTranchePaiement.UpdateTranchePaiementResponse;
import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.deleteTranchePaiement.DeleteTranchePaiementCommand;
import com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.deleteTranchePaiement.DeleteTranchePaiementResponse;
import com.example.BacK.application.g_Fournisseur.Query.tranchePaiement.GetTranchePaiementQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tranches-paiement")
@Tag(name = "Tranche Paiement", description = "Gestion des tranches de paiement")
public class TranchePaiementController {
    
    private final Mediator mediator;
    
    public TranchePaiementController(Mediator mediator) {
        this.mediator = mediator;
    }
    
    @PostMapping
    @Operation(summary = "Créer une nouvelle tranche de paiement")
    public ResponseEntity<List<AddTranchePaiementResponse>> createTranchePaiement(
            @Valid @RequestBody AddTranchePaiementCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une tranche de paiement")
    public ResponseEntity<List<UpdateTranchePaiementResponse>> updateTranchePaiement(
            @PathVariable String id, 
            @Valid @RequestBody UpdateTranchePaiementCommand command) {
        command.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }

    @PutMapping("/{trancheId}/payer")
    @Operation(summary = "Marquer une tranche comme payée")
    public ResponseEntity<List<PayTranchePaiementResponse>> payerTranche(@PathVariable String trancheId) {
        PayTranchePaiementCommand command = new PayTranchePaiementCommand(trancheId);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }
    
    @PostMapping("/search")
    @Operation(summary = "Rechercher et filtrer les tranches de paiement", 
               description = "Recherche des tranches selon des critères : par dette, fournisseur, statut (payé/non-payé), en retard, etc.")
    public ResponseEntity<List<Object>> searchTranchePaiements(@RequestBody GetTranchePaiementQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une tranche de paiement par son ID")
    public ResponseEntity<List<Object>> getTranchePaiementById(@PathVariable String id) {
        GetTranchePaiementQuery query = new GetTranchePaiementQuery();
        query.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une tranche de paiement")
    public ResponseEntity<List<DeleteTranchePaiementResponse>> deleteTranchePaiement(@PathVariable String id) {
        DeleteTranchePaiementCommand command = new DeleteTranchePaiementCommand(id);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }
}
