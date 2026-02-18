package com.example.BacK.presentation.Formation;

import com.example.BacK.application.g_Formation.Command.Evaluation.addEvaluation.AddEvaluationCommand;
import com.example.BacK.application.g_Formation.Command.Evaluation.deleteEvaluation.DeleteEvaluationCommand;
import com.example.BacK.application.g_Formation.Command.Evaluation.updateEvaluation.UpdateEvaluationCommand;
import com.example.BacK.application.g_Formation.Query.Evaluation.EvaluationsByContenuJour.GetEvaluationsByContenuJourQuery;
import com.example.BacK.application.g_Formation.Query.Evaluation.EvaluationsByContenuJour.GetEvaluationsByContenuJourResponse;
import com.example.BacK.application.g_Formation.Query.Evaluation.GetEvaluationQuery;
import com.example.BacK.application.g_Formation.Query.Evaluation.GetEvaluationResponse;
import com.example.BacK.application.mediator.Mediator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@Tag(name = "Évaluation", description = "Gestion des évaluations des formations et des apprenants")
public class EvaluationController {

    private final Mediator mediator;

    public EvaluationController(Mediator mediator) {
        this.mediator = mediator;
    }

    @Operation(summary = "Obtenir la liste des évaluations", description = "Retourne la liste complète des évaluations disponibles")
    @GetMapping
    public ResponseEntity<List<GetEvaluationResponse>> getAllEvaluations() {
        List<GetEvaluationResponse> responses = mediator.sendToHandlers(new GetEvaluationQuery(null));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtenir une évaluation par ID", description = "Retourne les détails d'une évaluation spécifique")
    @GetMapping("/{id}")
    public ResponseEntity<GetEvaluationResponse> getEvaluationById(@PathVariable Long id) {
        List<GetEvaluationResponse> responses = mediator.sendToHandlers(new GetEvaluationQuery(id));
        if (responses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Créer une nouvelle évaluation", description = "Ajoute une nouvelle évaluation pour un apprenant ou une formation")
    @PostMapping
    public ResponseEntity<Object> addEvaluation(@Valid @RequestBody AddEvaluationCommand command) {
        Object result = mediator.sendToHandlers(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/contenu/{idContenuJour}")
    public ResponseEntity<List<GetEvaluationsByContenuJourResponse>> getEvaluationsByContenuJour(
            @PathVariable Long idContenuJour) {

        List<GetEvaluationsByContenuJourResponse> response =
                mediator.sendToHandlers(new GetEvaluationsByContenuJourQuery(idContenuJour));

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Mettre à jour une évaluation", description = "Modifie les informations d'une évaluation existante")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvaluation(@PathVariable Long id, @Valid @RequestBody UpdateEvaluationCommand command) {
        command.setIdEvaluation(id);
        mediator.sendToHandlers(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Supprimer une évaluation", description = "Supprime une évaluation existante de la base de données")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        mediator.sendToHandlers(new DeleteEvaluationCommand(id));
        return ResponseEntity.noContent().build();
    }
}
