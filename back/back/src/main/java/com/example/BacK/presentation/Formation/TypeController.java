package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Type.addType.AddTypeCommand;
import com.example.BacK.application.g_Formation.Command.Type.deleteType.DeleteTypeCommand;
import com.example.BacK.application.g_Formation.Command.Type.updateType.UpdateTypeCommand;
import com.example.BacK.application.g_Formation.Query.PlanFormation.PlansByFormation.GetPlansByFormationQuery;
import com.example.BacK.application.g_Formation.Query.PlanFormation.PlansByFormation.GetPlansByFormationResponse;
import com.example.BacK.application.g_Formation.Query.Type.GetTypeQuery;
import com.example.BacK.application.g_Formation.Query.Type.GetTypeResponse;
import com.example.BacK.application.g_Formation.Query.Type.TypesByDomaine.GetTypesByDomaineQuery;
import com.example.BacK.application.g_Formation.Query.Type.TypesByDomaine.GetTypesByDomaineResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types")
@Tag(name = "Type", description = "Gestion des types (création, modification, consultation, suppression)")
public class TypeController {

    private final Mediator mediator;

    public TypeController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Obtenir un type par ID",
            description = "Retourne les détails complets d’un type spécifique à partir de son identifiant"
    )
    @GetMapping("/{id}")
    public ResponseEntity<GetTypeResponse> getTypeById(@PathVariable Long id) {
        GetTypeResponse response = (GetTypeResponse) mediator.sendToHandlers(new GetTypeQuery(id));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Créer un nouveau type",
            description = "Ajoute un type avec ses informations (nom, description, domaine)"
    )
    @PostMapping
    public ResponseEntity<Object> addType(@Valid @RequestBody AddTypeCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "Mettre à jour un type",
            description = "Modifie les informations d’un type existant à partir de son identifiant"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateType(@PathVariable Long id, @Valid @RequestBody UpdateTypeCommand command) {
        command.setIdType(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Obtenir la liste de tous les types",
            description = "Retourne la liste complète des types disponibles dans le système"
    )
    @GetMapping
    public ResponseEntity<List<GetTypeResponse>> getAllTypes() {
        List<GetTypeResponse> response =  mediator.sendToHandlers(new GetTypeQuery());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un type",
            description = "Supprime un type existant de la base de données"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteTypeCommand(id));
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Obtenir les types par domaine",
            description = "Retourne la liste des types associés à un domaine spécifique"
    )
    @GetMapping("/by-domaine/{idDomaine}")
    public ResponseEntity<List<GetTypesByDomaineResponse>> getTypesByDomaine(@PathVariable Long idDomaine) {
        List<GetTypesByDomaineResponse> response =
                mediator.sendToHandlers(new GetTypesByDomaineQuery(idDomaine));
        return ResponseEntity.ok(response);
    }

}
