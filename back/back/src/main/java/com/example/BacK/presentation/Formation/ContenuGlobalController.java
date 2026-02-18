package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.ContenuGlobal.addContenuGlobal.AddContenuGlobalCommand;
import com.example.BacK.application.g_Formation.Command.ContenuGlobal.deleteContenuGlobal.DeleteContenuGlobalCommand;
import com.example.BacK.application.g_Formation.Command.ContenuGlobal.updateContenuGlobal.UpdateContenuGlobalCommand;
import com.example.BacK.application.g_Formation.Query.ContenuGlobal.ContenusByFormation.GetContenusByFormationQuery;
import com.example.BacK.application.g_Formation.Query.ContenuGlobal.GetContenuGlobalQuery;
import com.example.BacK.application.g_Formation.Query.ContenuGlobal.GetContenuGlobalResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenusglobaux")
@Tag(name = "Contenu Global", description = "Gestion des contenus globaux liés aux formations")
public class ContenuGlobalController {

    private final Mediator mediator;

    public ContenuGlobalController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Obtenir la liste des contenus globaux", description = "Retourne tous les contenus globaux disponibles")
    @GetMapping
    public ResponseEntity<List<GetContenuGlobalResponse>> getAllContenusGlobaux() {
        List<GetContenuGlobalResponse> responses = mediator.sendToHandlers(new GetContenuGlobalQuery(null));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtenir un contenu global par ID", description = "Retourne les informations d’un contenu global spécifique")
    @GetMapping("/{id}")
    public ResponseEntity<GetContenuGlobalResponse> getContenuGlobalById(@PathVariable Long id) {
        GetContenuGlobalResponse response = (GetContenuGlobalResponse) mediator.sendToHandlers(new GetContenuGlobalQuery(id));
        return ResponseEntity.ok(response);
    }



    @Operation(summary = "Créer un contenu global", description = "Ajoute un nouveau contenu global lié à une formation")
    @PostMapping
    public ResponseEntity<Object> addContenuGlobal(@Valid @RequestBody AddContenuGlobalCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Mettre à jour un contenu global", description = "Modifie les informations d’un contenu global existant")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateContenuGlobal(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateContenuGlobalCommand command) {
        command.setIdContenuGlobal(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer un contenu global", description = "Supprime un contenu global existant de la base de données")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContenuGlobal(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteContenuGlobalCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtenir les contenus globaux d'une formation",
            description = "Retourne la liste des contenus globaux associés à une formation donnée"
    )
    @GetMapping("/by-formation/{idFormation}")
    public ResponseEntity<List<GetContenuGlobalResponse>> getContenusGlobauxByFormation(
            @PathVariable Long idFormation) {

        List<GetContenuGlobalResponse> responses =
                mediator.sendToHandlers(new GetContenusByFormationQuery(idFormation));

        return ResponseEntity.ok(responses);
    }


}
