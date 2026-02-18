package com.example.BacK.presentation.g_Fournisseur;

import com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.addDettesFournisseur.AddDettesFournisseurCommand;
import com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.addDettesFournisseur.AddDettesFournisseurResponse;
import com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.updateDettesFournisseur.UpdateDettesFournisseurCommand;
import com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.updateDettesFournisseur.UpdateDettesFournisseurResponse;
import com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.deleteDettesFournisseur.DeleteDettesFournisseurCommand;
import com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.deleteDettesFournisseur.DeleteDettesFournisseurResponse;
import com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.GetDettesFournisseurQuery;
import com.example.BacK.application.g_Fournisseur.Query.rapportDettes.RapportDettesEntrepriseQuery;
import com.example.BacK.application.g_Fournisseur.Query.rapportDettes.RapportDettesEntrepriseResponse;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
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
@RequestMapping("api/dettes-fournisseur")
@Tag(name = "Dettes Fournisseur", description = "API de gestion des dettes fournisseurs")
@SecurityRequirement(name = "bearerAuth")
public class DettesFournisseurController {

    private final Mediator mediator;
    private final IDettesFournisseurRepositoryService dettesFournisseurRepositoryService;

    public DettesFournisseurController(Mediator mediator, IDettesFournisseurRepositoryService dettesFournisseurRepositoryService) {
        this.mediator = mediator;
        this.dettesFournisseurRepositoryService = dettesFournisseurRepositoryService;
    }

    @Operation(summary = "Ajouter une dette fournisseur", description = "Crée une nouvelle dette fournisseur")
    @PostMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_CREER')")
    public ResponseEntity<List<AddDettesFournisseurResponse>> add(@Valid @RequestBody AddDettesFournisseurCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Mettre à jour une dette fournisseur", description = "Met à jour une dette fournisseur existante")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_MODIFIER')")
    public ResponseEntity<List<UpdateDettesFournisseurResponse>> update(@PathVariable String id, 
                                                                         @Valid @RequestBody UpdateDettesFournisseurCommand command) {
        command.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(command));
    }

    @Operation(summary = "Supprimer une dette fournisseur", description = "Supprime une dette fournisseur par son ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_SUPPRIMER')")
    public ResponseEntity<List<DeleteDettesFournisseurResponse>> delete(@PathVariable String id) {
        return ResponseEntity.ok(mediator.sendToHandlers(new DeleteDettesFournisseurCommand(id)));
    }

    @Operation(summary = "Récupérer une dette fournisseur par ID", description = "Retourne une dette fournisseur spécifique")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_LIRE')")
    public ResponseEntity<List<Object>> getById(@PathVariable String id) {
        GetDettesFournisseurQuery query = new GetDettesFournisseurQuery();
        query.setId(id);
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }

    @Operation(summary = "Rechercher et filtrer les dettes fournisseurs", 
               description = "Recherche unifiée pour toutes les dettes fournisseurs avec support de filtres multiples: " +
                           "par ID, fournisseur, statut de paiement, dates, etc. " +
                           "Support de pagination: page (défaut: 0), size (défaut: 10), sortBy (défaut: createdAt), sortDirection (ASC/DESC)")
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('FOURNISSEUR_LIRE')")
    public ResponseEntity<List<Object>> search(@RequestBody GetDettesFournisseurQuery query) {
        return ResponseEntity.ok(mediator.sendToHandlers(query));
    }
    
    @Operation(
        summary = "Générer un rapport détaillé des dettes de l'entreprise", 
        description = "Génère un rapport complet et détaillé sur toutes les dettes de l'entreprise en français. "
    )
    @GetMapping("/rapport")
    @PreAuthorize("hasAuthority('FOURNISSEUR_EXPORTER')")
    public ResponseEntity<RapportDettesEntrepriseResponse> genererRapportDettes(@ModelAttribute RapportDettesEntrepriseQuery query) {
        List<Object> results = mediator.sendToHandlers(query);
        
        if (results != null && !results.isEmpty() && results.get(0) instanceof RapportDettesEntrepriseResponse) {
            return ResponseEntity.ok((RapportDettesEntrepriseResponse) results.get(0));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
