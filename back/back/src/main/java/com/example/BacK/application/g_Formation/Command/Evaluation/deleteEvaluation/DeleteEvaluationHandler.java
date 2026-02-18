package com.example.BacK.application.g_Formation.Command.Evaluation.deleteEvaluation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.EvaluationRepositoryService;
import org.springframework.stereotype.Component;


@Component("DeleteEvaluationHandler")
public class DeleteEvaluationHandler implements RequestHandler<DeleteEvaluationCommand, Void> {

    private final EvaluationRepositoryService evaluationRepositoryService;

    public DeleteEvaluationHandler(EvaluationRepositoryService evaluationRepositoryService) {
        this.evaluationRepositoryService = evaluationRepositoryService;
    }

    @Override
    public Void handle(DeleteEvaluationCommand command) {
        if (command.getIdEvaluation() == null) {
            throw new IllegalArgumentException("L'ID de l'évaluation ne peut pas être nul.");
        }

        if (!evaluationRepositoryService.existsById(command.getIdEvaluation())) {
            throw new IllegalArgumentException("Aucune évaluation trouvée avec l’ID : " + command.getIdEvaluation());
        }

        evaluationRepositoryService.deleteEvaluation(command.getIdEvaluation());
        return null;
    }
}
