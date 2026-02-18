package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.PlanFormation.addPlanFormation.AddPlanFormationCommand;
import com.example.BacK.application.g_Formation.Command.PlanFormation.deletePlanFormation.DeletePlanFormationCommand;
import com.example.BacK.application.g_Formation.Command.PlanFormation.updatePlanFormation.UpdatePlanFormationCommand;
import com.example.BacK.application.g_Formation.Query.PlanFormation.GetPlanFormationQuery;
import com.example.BacK.application.g_Formation.Query.PlanFormation.GetPlanFormationResponse;
import com.example.BacK.application.g_Formation.Query.PlanFormation.PlansByFormation.GetPlansByFormationQuery;
import com.example.BacK.application.g_Formation.Query.PlanFormation.PlansByFormation.GetPlansByFormationResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planformations")
@Tag(name = "PlanFormation", description = "Gestion des plans de formation (création, modification, consultation, suppression)")
public class PlanFormationController {

    private final Mediator mediator;

    public PlanFormationController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(
            summary = "Obtenir la liste des plans de formation",
            description = "Retourne la liste complète des plans de formation enregistrés"
    )
    @GetMapping
    public ResponseEntity<List<GetPlanFormationResponse>> getAllPlanFormations() {
        List<GetPlanFormationResponse> plans = mediator.sendToHandlers(new GetPlanFormationQuery(null));
        return ResponseEntity.ok(plans);
    }

    @Operation(
            summary = "Obtenir un plan de formation par ID",
            description = "Retourne les détails d’un plan de formation spécifique"
    )
    @GetMapping("/{id}")
    public ResponseEntity<List<GetPlanFormationResponse>> getPlanFormationById(@PathVariable Long id) {
        List<GetPlanFormationResponse> plan = mediator.sendToHandlers(new GetPlanFormationQuery(id));
        return ResponseEntity.ok(plan);
    }

    @Operation(
            summary = "Créer un plan de formation",
            description = "Ajoute un nouveau plan de formation avec ses informations"
    )
    @PostMapping
    public ResponseEntity<Object> addPlanFormation(@Valid @RequestBody AddPlanFormationCommand command) {
        Object response = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Mettre à jour un plan de formation",
            description = "Modifie les informations d’un plan existant à partir de son identifiant"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlanFormation(@PathVariable Long id,
                                                    @Valid @RequestBody UpdatePlanFormationCommand command) {
        command.setIdPlanFormation(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Supprimer un plan de formation",
            description = "Supprime un plan de formation de la base de données"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanFormation(@PathVariable Long id) {
        mediator.sendToHandlers(new DeletePlanFormationCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtenir les plans d'une formation spécifique",
            description = "Retourne tous les plans liés à une formation donnée (via son idFormation)"
    )
    @GetMapping("/formation/{idFormation}")
    public ResponseEntity<List<GetPlansByFormationResponse>> getPlansByFormation(@PathVariable Long idFormation) {
        List<GetPlansByFormationResponse> plans = mediator.sendToHandlers(new GetPlansByFormationQuery(idFormation));
        return ResponseEntity.ok(plans);
    }




}
