package com.example.BacK.application.g_Formation.Query.Examen;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.ApprenantDTO;
import com.example.BacK.application.models.g_formation.PlanFormationDTO;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Examen;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.ExamenRepositoryService;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetExamenHandler")
public class GetExamenHandler implements RequestHandler<GetExamenQuery, List<GetExamenResponse>> {

    private final ExamenRepositoryService examenRepositoryService;

    public GetExamenHandler(ExamenRepositoryService examenRepositoryService) {
        this.examenRepositoryService = examenRepositoryService;
    }

    @Override
    public List<GetExamenResponse> handle(GetExamenQuery query) {

        if (query.getIdExamen() != null) {

            return examenRepositoryService.getExamenById(query.getIdExamen())
                    .map(ex -> Collections.singletonList(mapToResponse(ex)))
                    .orElse(Collections.emptyList());
        }

        return examenRepositoryService.getAllExamens()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetExamenResponse mapToResponse(Examen ex) {

        GetExamenResponse dto = new GetExamenResponse();

        dto.setIdExamen(ex.getIdExamen());
        dto.setType(ex.getType());
        dto.setDate(ex.getDate());
        dto.setDescription(ex.getDescription());
        dto.setScore(ex.getScore());

        if (ex.getApprenant() != null) {
            Apprenant a = ex.getApprenant();
            ApprenantDTO adto = new ApprenantDTO();

            adto.setIdApprenant(a.getId());
            adto.setNom(a.getNom());
            adto.setPrenom(a.getPrenom());
            adto.setEmail(a.getEmail());
            adto.setTelephone(a.getTelephone());

            if (a.getPlanFormation() != null) {
                adto.setIdPlanFormation(a.getPlanFormation().getIdPlanFormation());
            }

            dto.setApprenant(adto);
        }

        PlanFormation pf = ex.getPlanFormation();

        if (pf == null && ex.getApprenant() != null) {
            pf = ex.getApprenant().getPlanFormation();
        }

        if (pf != null) {
            PlanFormationDTO pfDto = new PlanFormationDTO();
            pfDto.setIdPlanFormation(pf.getIdPlanFormation());
            pfDto.setTitre(pf.getTitre());
            dto.setPlanFormation(pfDto);
        }

        return dto;
    }
}
