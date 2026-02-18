package com.example.BacK.application.g_Formation.Query.Evaluation;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Evaluation;
import com.example.BacK.infrastructure.services.g_Formation.EvaluationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetEvaluationHandler")
public class GetEvaluationHandler implements RequestHandler<GetEvaluationQuery, List<GetEvaluationResponse>> {

    private final EvaluationRepositoryService evaluationRepositoryService;
    private final ModelMapper modelMapper;

    public GetEvaluationHandler(EvaluationRepositoryService evaluationRepositoryService, ModelMapper modelMapper) {
        this.evaluationRepositoryService = evaluationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetEvaluationResponse> handle(GetEvaluationQuery command) {

        if (command.getIdEvaluation() != null) {
            Evaluation evaluation = evaluationRepositoryService.getEvaluationById(command.getIdEvaluation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucune évaluation trouvée avec l'ID : " + command.getIdEvaluation()
                    ));

            return List.of(mapToResponse(evaluation));
        }

        List<Evaluation> evaluations = evaluationRepositoryService.getAllEvaluations();

        return evaluations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetEvaluationResponse mapToResponse(Evaluation evaluation) {
        GetEvaluationResponse response = modelMapper.map(evaluation, GetEvaluationResponse.class);

        if (evaluation.getPlanFormation() != null) {
            response.setPlanFormationId(evaluation.getPlanFormation().getIdPlanFormation());
        }

        if (evaluation.getContenuJourFormation() != null) {
            response.setContenuJourFormationId(evaluation.getContenuJourFormation().getIdContenuJour());
        }

        if (evaluation.getApprenant() != null) {
            response.setApprenantId(evaluation.getApprenant().getId());
        }

        return response;
    }
}
