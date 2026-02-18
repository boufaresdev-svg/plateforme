package com.example.BacK.application.g_Formation.Query.ContenuGlobal.ContenusByFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuGlobal;
import com.example.BacK.infrastructure.services.g_Formation.ContenuGlobalRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetContenusByFormationHandler")
public class GetContenusByFormationHandler implements RequestHandler<GetContenusByFormationQuery, List<GetContenusByFormationResponse>> {

    private final ContenuGlobalRepositoryService contenuService;

    public GetContenusByFormationHandler(ContenuGlobalRepositoryService contenuService) {
        this.contenuService = contenuService;
    }

    @Override
    public List<GetContenusByFormationResponse> handle(GetContenusByFormationQuery query) {

        List<ContenuGlobal> contenus =
                contenuService.getContenusByFormation(query.getIdFormation());

        return contenus.stream()
                .map(c -> new GetContenusByFormationResponse(
                        c.getIdContenuGlobal(),
                        c.getTitre(),
                        c.getDescription()
                ))
                .collect(Collectors.toList());
    }
}
