package com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantClasses;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Classe;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ClasseRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetApprenantClassesHandler")
public class GetApprenantClassesHandler implements RequestHandler<GetApprenantClassesQuery, GetApprenantClassesResponse> {

    private final ApprenantRepositoryService apprenantRepositoryService;
    private final ClasseRepositoryService classeRepositoryService;

    public GetApprenantClassesHandler(ApprenantRepositoryService apprenantRepositoryService,
                                      ClasseRepositoryService classeRepositoryService) {
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.classeRepositoryService = classeRepositoryService;
    }

    @Override
    public GetApprenantClassesResponse handle(GetApprenantClassesQuery query) {
        // Validate apprenant exists
        if (!apprenantRepositoryService.existsById(query.getApprenantId())) {
            throw new IllegalArgumentException("Apprenant non trouvé avec l'ID: " + query.getApprenantId());
        }

        // Get classes for this apprenant
        List<Classe> classes = classeRepositoryService.getClassesByApprenant(query.getApprenantId());

        // Map to response
        List<GetApprenantClassesResponse.ClasseInfo> classesInfo = classes.stream()
                .map(this::mapToClasseInfo)
                .collect(Collectors.toList());

        GetApprenantClassesResponse response = new GetApprenantClassesResponse();
        response.setApprenantId(query.getApprenantId());
        response.setTotalClasses(classes.size());
        response.setClasses(classesInfo);

        return response;
    }

    private GetApprenantClassesResponse.ClasseInfo mapToClasseInfo(Classe classe) {
        GetApprenantClassesResponse.ClasseInfo info = new GetApprenantClassesResponse.ClasseInfo();
        info.setId(classe.getId());
        info.setNom(classe.getNom());
        info.setCode(classe.getCode());
        info.setDescription(classe.getDescription());
        info.setCapaciteMax(classe.getCapaciteMax());
        info.setIsActive(classe.getIsActive());
        info.setDateDebutAcces(classe.getDateDebutAcces());
        info.setDateFinAcces(classe.getDateFinAcces());
        info.setNombreInscrits(classe.getApprenants() != null ? classe.getApprenants().size() : 0);

        // Map formation info - either from direct link or via planFormation
        if (classe.getFormation() != null) {
            GetApprenantClassesResponse.FormationInfo formationInfo = new GetApprenantClassesResponse.FormationInfo();
            formationInfo.setId(classe.getFormation().getIdFormation());
            formationInfo.setNom(classe.getFormation().getTheme());
            info.setFormation(formationInfo);
        } else if (classe.getPlanFormation() != null && classe.getPlanFormation().getFormation() != null) {
            // Resolve formation through planFormation
            GetApprenantClassesResponse.FormationInfo formationInfo = new GetApprenantClassesResponse.FormationInfo();
            formationInfo.setId(classe.getPlanFormation().getFormation().getIdFormation());
            formationInfo.setNom(classe.getPlanFormation().getFormation().getTheme());
            info.setFormation(formationInfo);
        }

        // Map plan formation info if available
        if (classe.getPlanFormation() != null) {
            GetApprenantClassesResponse.PlanFormationInfo planInfo = new GetApprenantClassesResponse.PlanFormationInfo();
            planInfo.setId(classe.getPlanFormation().getIdPlanFormation());
            planInfo.setNom(classe.getPlanFormation().getTitre());
            info.setPlanFormation(planInfo);
        }

        return info;
    }
}
