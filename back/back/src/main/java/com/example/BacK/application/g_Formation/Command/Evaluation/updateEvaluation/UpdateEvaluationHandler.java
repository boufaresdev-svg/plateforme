package com.example.BacK.application.g_Formation.Command.Evaluation.updateEvaluation;



import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.domain.g_Formation.Evaluation;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.EvaluationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateEvaluationHandler")
public class UpdateEvaluationHandler implements RequestHandler<UpdateEvaluationCommand, Void> {

    private final EvaluationRepositoryService evaluationRepositoryService;
    private final PlanFormationRepositoryService planFormationRepositoryService;
    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ApprenantRepositoryService apprenantRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateEvaluationHandler(EvaluationRepositoryService evaluationRepositoryService,
                                   PlanFormationRepositoryService planFormationRepositoryService,
                                   ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
                                   ApprenantRepositoryService apprenantRepositoryService,
                                   ModelMapper modelMapper) {
        this.evaluationRepositoryService = evaluationRepositoryService;
        this.planFormationRepositoryService = planFormationRepositoryService;
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateEvaluationCommand command) {
        if (command.getIdEvaluation() == null) {
            throw new IllegalArgumentException("L'ID de l'évaluation ne peut pas être nul.");
        }

        Evaluation evaluation = evaluationRepositoryService.getEvaluationById(command.getIdEvaluation())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune évaluation trouvée avec l'ID : " + command.getIdEvaluation()
                ));

        evaluation.setType(command.getType());
        evaluation.setDate(command.getDate());
        evaluation.setDescription(command.getDescription());
        evaluation.setScore(command.getScore());

        if (command.getIdPlanFormation() != null) {
            PlanFormation planFormation = planFormationRepositoryService.getPlanFormationById(command.getIdPlanFormation())
                    .orElseThrow(() -> new IllegalArgumentException("Plan de formation introuvable avec l'ID : " + command.getIdPlanFormation()));
            evaluation.setPlanFormation(planFormation);
        }

        if (command.getIdContenuJourFormation() != null) {
            ContenuJourFormation contenu = contenuJourFormationRepositoryService.getContenuJourFormationById(command.getIdContenuJourFormation())
                    .orElseThrow(() -> new IllegalArgumentException("Contenu jour formation introuvable avec l'ID : " + command.getIdContenuJourFormation()));
            evaluation.setContenuJourFormation(contenu);
        }

        if (command.getIdApprenant() != null) {
            Apprenant apprenant = apprenantRepositoryService.getApprenantById(command.getIdApprenant())
                    .orElseThrow(() -> new IllegalArgumentException("Apprenant introuvable avec l'ID : " + command.getIdApprenant()));
            evaluation.setApprenant(apprenant);
        }

        evaluationRepositoryService.saveEvaluation(evaluation);
        return null;
    }
}
