package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.addContenuJourFormation.AddContenuJourFormationCommand;
import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.deleteContenuJourFormation.DeleteContenuJourFormationCommand;
import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.updateContenuJourFormation.UpdateContenuJourFormationCommand;
import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.assignContenuDetaille.AssignContenuDetailleCommand;
import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.assignContenuDetaille.AssignContenuDetailleRequest;
import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.assignContenuDetaille.AssignContenuDetailleResponse;
import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.copyAndLink.CopyAndLinkContenuJourCommand;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.ContenusByObjectif.GetContenusByObjectifQuery;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.ContenusByObjectif.GetContenusByObjectifResponse;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.GetContenuJourFormationQuery;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.GetContenuJourFormationResponse;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.GetAssignedContenus.GetAssignedContenusQuery;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.GetAssignedContenus.GetAssignedContenusResponse;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.SearchContenuJourFormation.SearchContenuJourFormationQuery;
import com.example.BacK.application.g_Formation.Query.ContenuJourFormation.SearchContenuJourFormation.SearchContenuJourFormationResponse;
import com.example.BacK.application.mediator.Mediator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenusjour")
@Tag(name = "ContenuJourFormation", description = "Gestion des contenus par jour de formation")
public class ContenuJourFormationController {

    private final Mediator mediator;

    public ContenuJourFormationController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Lister tous les contenus de jour")
    @GetMapping
    public ResponseEntity<List<GetContenuJourFormationResponse>> getAll() {
        List<GetContenuJourFormationResponse> responses =
                mediator.sendToHandlers(new GetContenuJourFormationQuery(null));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtenir un contenu de jour par ID")
    @GetMapping("/{id}")
    public ResponseEntity<GetContenuJourFormationResponse> getById(@PathVariable Long id) {
        GetContenuJourFormationResponse response =
                (GetContenuJourFormationResponse) mediator.sendToHandlers(
                        new GetContenuJourFormationQuery(id));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister les contenus journaliers d'un objectif spécifique")
    @GetMapping("/by-objectif/{idObjectifSpec}")
    public ResponseEntity<List<GetContenusByObjectifResponse>> getContenusByObjectif(
            @PathVariable Long idObjectifSpec) {

        List<GetContenusByObjectifResponse> responses =
                mediator.sendToHandlers(new GetContenusByObjectifQuery(idObjectifSpec));

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Créer un contenu de jour",
            description = "⚠️ Vous devez fournir : contenu, heures, idObjectifSpecifique ET idPlanFormation"
    )
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AddContenuJourFormationCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "Mettre à jour un contenu de jour",
            description = "⚠️ idPlanFormation doit également être fourni sinon une erreur est générée"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContenuJourFormationCommand command) {

        command.setIdContenuJour(id);
        mediator.sendToHandlers(command);

        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer un contenu de jour")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteContenuJourFormationCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Chercher des contenus de jour",
            description = "Recherche des contenus de jour par texte, objectif spécifique, et statut (copié/transféré). " +
                    "isCopied: true = copié (lié à un objectif spécifique), false = transféré, null = tous"
    )
    @GetMapping("/search")
    public ResponseEntity<List<SearchContenuJourFormationResponse>> searchContenusJour(
            @RequestParam(value = "contenu", required = false) String contenu,
            @RequestParam(value = "idObjectifSpecifique", required = false) Long idObjectifSpecifique,
            @RequestParam(value = "isCopied", required = false) Boolean isCopied) {
        
        SearchContenuJourFormationQuery query = new SearchContenuJourFormationQuery(
                contenu, idObjectifSpecifique, isCopied
        );
        
        List<SearchContenuJourFormationResponse> responses =
                mediator.sendToHandlers(query);
        
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Assigner des contenus détaillés à un contenu de jour",
            description = "Assigne une liste de contenus détaillés à un contenu jour de formation"
    )
    @PostMapping("/{id}/assign-contenus")
    public ResponseEntity<Object> assignContenus(
            @PathVariable Long id,
            @RequestBody AssignContenuDetailleRequest request) {
        
        if (id == null) {
            return ResponseEntity.badRequest().body("ID cannot be null");
        }
        
        try {
            AssignContenuDetailleCommand command = new AssignContenuDetailleCommand();
            command.setIdContenuJour(id);
            command.setIdContenusDetailles(request.getIdContenusDetailles());
            command.setNiveau(request.getNiveau());
            command.setNiveauLabel(request.getNiveauLabel());
            
            Object response = mediator.sendToHandlers(command);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("message", "Erreur lors de l'assignation: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Obtenir les contenus détaillés assignés à un contenu de jour",
            description = "Récupère la liste des contenus détaillés assignés"
    )
    @GetMapping("/{id}/assigned-contenus")
    public ResponseEntity<?> getAssignedContenus(@PathVariable Long id) {
        Object responses = mediator.sendToHandlers(new GetAssignedContenusQuery(id));
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Copier et lier un contenu de jour à un objectif spécifique",
            description = "Crée une copie d'un contenu de jour existant et la lie à un objectif spécifique dans une formation"
    )
    @PostMapping("/copy-and-link")
    public ResponseEntity<Void> copyAndLinkContenuJour(@RequestBody CopyAndLinkContenuJourCommand command) {
        mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Mettre à jour l'ordre d'un contenu de jour")
    @PatchMapping("/{id}/ordre")
    public ResponseEntity<Void> updateOrdre(
            @PathVariable Long id,
            @RequestParam Integer ordre) {
        // This would need a dedicated command, but for simplicity we can call service directly
        // In a proper CQRS, create UpdateContenuOrdreCommand
        return ResponseEntity.ok().build();
    }
}
