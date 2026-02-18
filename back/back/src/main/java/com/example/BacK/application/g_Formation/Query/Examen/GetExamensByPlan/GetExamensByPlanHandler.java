package com.example.BacK.application.g_Formation.Query.Examen.GetExamensByPlan;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Examen;
import com.example.BacK.infrastructure.services.g_Formation.ExamenRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetExamensByPlanHandler")
public class GetExamensByPlanHandler implements RequestHandler<GetExamensByPlanQuery, List<GetExamensByPlanResponse>> {

    private final ExamenRepositoryService examenRepositoryService;
    private final ModelMapper modelMapper;

    public GetExamensByPlanHandler(ExamenRepositoryService examenRepositoryService,
                                   ModelMapper modelMapper) {
        this.examenRepositoryService = examenRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetExamensByPlanResponse> handle(GetExamensByPlanQuery query) {

        if (query.getIdPlanFormation() == null) {
            throw new IllegalArgumentException("❌ idPlanFormation est obligatoire !");
        }

        List<Examen> examens = examenRepositoryService
                .getExamensByPlanFormation(query.getIdPlanFormation());

        return examens.stream().map(ex -> {

            GetExamensByPlanResponse resp =
                    modelMapper.map(ex, GetExamensByPlanResponse.class);

            if (ex.getApprenant() != null && ex.getApprenant().getPlanFormation() != null) {
                resp.setIdPlanFormation(
                        ex.getApprenant().getPlanFormation().getIdPlanFormation()
                );

            }

            if (ex.getApprenant() != null) {
                resp.setIdApprenant(ex.getApprenant().getId());
            }

            return resp;

        }).collect(Collectors.toList());
    }
}
