package com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantsByPlanFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.enumEntity.StatusInscription;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetApprenantsByPlanFormationHandler")
public class GetApprenantsByPlanFormationHandler implements
        RequestHandler<GetApprenantsByPlanFormationQuery, List<GetApprenantsByPlanFormationResponse>> {

    private final ApprenantRepositoryService apprenantRepositoryService;

    public GetApprenantsByPlanFormationHandler(ApprenantRepositoryService apprenantRepositoryService) {
        this.apprenantRepositoryService = apprenantRepositoryService;
    }

    @Override
    public List<GetApprenantsByPlanFormationResponse> handle(GetApprenantsByPlanFormationQuery query) {

        List<Apprenant> apprenants = apprenantRepositoryService.findByPlanFormationId(query.getPlanFormationId());

        return apprenants.stream()
                .map(a -> new GetApprenantsByPlanFormationResponse(
                        a.getId(),
                        a.getNom(),
                        a.getPrenom(),
                        a.getEmail(),
                        a.getTelephone(),
                        a.getPrerequis(),
                        a.getAdresse(),
                        a.getStatusInscription() != null ? StatusInscription.valueOf(a.getStatusInscription().name()) : null,
                        a.getId()
                ))
                .collect(Collectors.toList());
    }
}

