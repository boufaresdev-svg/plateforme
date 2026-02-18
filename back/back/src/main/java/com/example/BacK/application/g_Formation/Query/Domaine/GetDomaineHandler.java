package com.example.BacK.application.g_Formation.Query.Domaine;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.DomaineRepositoryService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetDomaineHandler")
public class GetDomaineHandler implements RequestHandler<GetDomaineQuery, List<GetDomaineResponse>> {

    private final DomaineRepositoryService domaineRepositoryService;

    public GetDomaineHandler(DomaineRepositoryService domaineRepositoryService) {
        this.domaineRepositoryService = domaineRepositoryService;
    }

    @Override
    public List<GetDomaineResponse> handle(GetDomaineQuery command) {

        return domaineRepositoryService.getAllDomaines().stream()

                .map(domaine -> new GetDomaineResponse(domaine.getIdDomaine(), domaine.getNom(), domaine.getDescription()))
                .collect(Collectors.toList());
    }
}
