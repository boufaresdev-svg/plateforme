package com.example.BacK.application.g_Formation.Query.Apprenant;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetApprenantHandler")
public class GetApprenantHandler implements RequestHandler<GetApprenantQuery, List<GetApprenantResponse>> {

    private final ApprenantRepositoryService apprenantRepositoryService;
    private final ModelMapper modelMapper;

    public GetApprenantHandler(ApprenantRepositoryService apprenantRepositoryService, ModelMapper modelMapper) {
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetApprenantResponse> handle(GetApprenantQuery command) {

        if (command.getIdApprenant() != null) {
            Apprenant apprenant = apprenantRepositoryService.getApprenantById(command.getIdApprenant())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun apprenant trouvé avec l’ID : " + command.getIdApprenant()
                    ));

            return List.of(mapToResponse(apprenant));
        }

        List<Apprenant> apprenants = apprenantRepositoryService.getAllApprenants();

        return apprenants.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetApprenantResponse mapToResponse(Apprenant apprenant) {
        GetApprenantResponse response = modelMapper.map(apprenant, GetApprenantResponse.class);

        // Map plan formation details
        if (apprenant.getPlanFormation() != null) {
            response.setPlanFormationId(apprenant.getPlanFormation().getIdPlanFormation());
            response.setPlanFormationTitre(apprenant.getPlanFormation().getTitre());
        }

        // Map new fields explicitly to ensure proper mapping
        // ModelMapper should handle most fields automatically, but set explicitly for safety
        response.setMatricule(apprenant.getMatricule());
        response.setIsBlocked(apprenant.getIsBlocked() != null ? apprenant.getIsBlocked() : false);
        response.setIsStaff(apprenant.getIsStaff() != null ? apprenant.getIsStaff() : false);
        response.setIsActive(apprenant.getIsActive() != null ? apprenant.getIsActive() : true);
        response.setCreatedAt(apprenant.getCreatedAt());
        response.setUpdatedAt(apprenant.getUpdatedAt());
        response.setLastLogin(apprenant.getLastLogin());

        return response;
    }
}
