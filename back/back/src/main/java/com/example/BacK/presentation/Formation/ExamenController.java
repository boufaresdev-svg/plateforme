package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Evaluation.addEvaluation.AddEvaluationCommand;
import com.example.BacK.application.g_Formation.Command.Examen.addExamen.AddExamenCommand;
import com.example.BacK.application.g_Formation.Command.Examen.deleteExamen.DeleteExamenCommand;
import com.example.BacK.application.g_Formation.Command.Examen.updateExamen.UpdateExamenCommand;
import com.example.BacK.application.g_Formation.Query.Evaluation.GetEvaluationQuery;
import com.example.BacK.application.g_Formation.Query.Evaluation.GetEvaluationResponse;
import com.example.BacK.application.g_Formation.Query.Examen.GetExamenQuery;
import com.example.BacK.application.g_Formation.Query.Examen.GetExamenResponse;
import com.example.BacK.application.g_Formation.Query.Examen.GetExamensByPlan.GetExamensByPlanQuery;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/examens")
@Tag(name = "Examen", description = "API de gestion des examens dans le module de formation")
public class ExamenController {

    private final Mediator mediator;

    public ExamenController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Ajouter un examen", description = "Crée un nouvel examen associé à un apprenant et éventuellement à un certificat.")
    @PostMapping
    public ResponseEntity<Object> addExamen(@Valid @RequestBody AddExamenCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Mettre à jour un examen", description = "Met à jour les informations d’un examen existant.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateExamen(@PathVariable Long id, @Valid @RequestBody UpdateExamenCommand command) {
        command.setIdExamen(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer un examen", description = "Supprime un examen à partir de son identifiant.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamen(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteExamenCommand(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtenir un examen par ID", description = "Récupère les détails d’un examen à partir de son identifiant.")
    @GetMapping("/{id}")
    public ResponseEntity<GetExamenResponse> getExamenById(@PathVariable Long id) {
        GetExamenResponse response = (GetExamenResponse) mediator.sendToHandlers(new GetExamenQuery(id));
        return (response == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister tous les examens", description = "Retourne la liste de tous les examens enregistrés.")
    @GetMapping
    public ResponseEntity<List<GetExamenResponse>> getAllExamens() {
        List<GetExamenResponse> responses = mediator.sendToHandlers(new GetExamenQuery());
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Obtenir les examens d’un plan de formation",
            description = "Retourne tous les examens appartenant aux apprenants inscrits dans un plan de formation donné."
    )
    @GetMapping("/plan/{idPlanFormation}")
    public ResponseEntity<List<GetExamenResponse>> getExamensByPlan(@PathVariable Long idPlanFormation) {

        List<GetExamenResponse> responses =
                mediator.sendToHandlers(new GetExamensByPlanQuery(idPlanFormation));

        return ResponseEntity.ok(responses);
    }



}

