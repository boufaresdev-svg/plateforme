package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Domaine.addDomaine.AddDomaineCommand;
import com.example.BacK.application.g_Formation.Command.Domaine.deleteDomaine.DeleteDomaineCommand;
import com.example.BacK.application.g_Formation.Command.Domaine.updateDomaine.UpdateDomaineCommand;
import com.example.BacK.application.g_Formation.Command.Formateur.addFormateur.AddFormateurCommand;
import com.example.BacK.application.g_Formation.Query.Domaine.GetDomaineQuery;
import com.example.BacK.application.g_Formation.Query.Domaine.GetDomaineResponse;
import com.example.BacK.application.mediator.Mediator;
import com.example.BacK.application.models.g_formation.DomaineDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/domaines")
@Tag(name = "Domaine", description = "Gestion des domaines (création, modification, consultation, suppression)")
public class DomaineController {

    private final Mediator mediator;

    public DomaineController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Obtenir un domaine par ID",
            description = "Retourne les détails d’un domaine spécifique à partir de son identifiant"
    )
    @GetMapping("/{id}")
    public ResponseEntity<GetDomaineResponse> getDomaineById(@PathVariable Long id) {
        GetDomaineResponse response = (GetDomaineResponse) mediator.sendToHandlers(new GetDomaineQuery(id));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtenir tous les domaines",
            description = "Retourne la liste complète des domaines"
    )
    @GetMapping
    public ResponseEntity<List<GetDomaineResponse>> getAllDomaines() {
        List<GetDomaineResponse> domaines = mediator.sendToHandlers(new GetDomaineQuery(null));
        return ResponseEntity.ok(domaines);
    }

    @Operation(
            summary = "Créer un nouveau domaine",
            description = "Ajoute un domaine avec son nom et sa description"
    )
    @PostMapping
    public ResponseEntity<Object> addDomaine(@Valid @RequestBody AddDomaineCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "Mettre à jour un domaine",
            description = "Modifie les informations d’un domaine existant à partir de son identifiant"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDomaine(@PathVariable Long id, @Valid @RequestBody UpdateDomaineCommand command) {
        command.setIdDomaine(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un domaine",
            description = "Supprime un domaine existant de la base de données"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDomaine(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteDomaineCommand(id));
        return ResponseEntity.noContent().build();
    }
}
