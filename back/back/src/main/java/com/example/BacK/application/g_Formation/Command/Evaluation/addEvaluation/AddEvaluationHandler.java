package com.example.BacK.application.g_Formation.Command.Evaluation.addEvaluation;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.domain.g_Formation.Evaluation;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.EvaluationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddEvaluationHandler")
public class AddEvaluationHandler implements RequestHandler<AddEvaluationCommand, AddEvaluationResponse> {

    private final EvaluationRepositoryService evaluationRepositoryService;
    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ApprenantRepositoryService apprenantRepositoryService;
    private final ModelMapper modelMapper;

    public AddEvaluationHandler(
            EvaluationRepositoryService evaluationRepositoryService,
            ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
            ApprenantRepositoryService apprenantRepositoryService,
            ModelMapper modelMapper) {

        this.evaluationRepositoryService = evaluationRepositoryService;
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddEvaluationResponse handle(AddEvaluationCommand command) {

        Evaluation evaluation = modelMapper.map(command, Evaluation.class);
        evaluation.setIdEvaluation(null);

        ContenuJourFormation contenuJour = contenuJourFormationRepositoryService
                .getContenuJourFormationById(command.getIdContenuJourFormation())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun contenu jour trouvé avec l’ID : " + command.getIdContenuJourFormation()));

        evaluation.setContenuJourFormation(contenuJour);

        if (contenuJour.getPlanFormation() != null) {
            evaluation.setPlanFormation(contenuJour.getPlanFormation());
        }

        Apprenant apprenant = apprenantRepositoryService
                .getApprenantById(command.getIdApprenant())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun apprenant trouvé avec l’ID : " + command.getIdApprenant()));

        evaluation.setApprenant(apprenant);

        Evaluation saved = evaluationRepositoryService.saveEvaluation(evaluation);

        return new AddEvaluationResponse(saved.getIdEvaluation(), "Évaluation ajoutée avec succès !");
    }
}

