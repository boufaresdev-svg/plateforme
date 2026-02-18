package com.example.BacK.application.g_Formation.Command.Domaine.addDomaine;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Domaine;
import com.example.BacK.infrastructure.services.g_Formation.DomaineRepositoryService;
import org.springframework.stereotype.Component;

@Component("AddDomaineHandler")
public class AddDomaineHandler implements RequestHandler<AddDomaineCommand, AddDomaineResponse> {

    private final DomaineRepositoryService domaineRepositoryService;

    public AddDomaineHandler(DomaineRepositoryService domaineRepositoryService) {
        this.domaineRepositoryService = domaineRepositoryService;
    }

    @Override
    public AddDomaineResponse handle(AddDomaineCommand command) {

        Domaine domaine = new Domaine();
        domaine.setNom(command.getNom());
        domaine.setDescription(command.getDescription());
        Domaine savedDomaine = domaineRepositoryService.saveDomaine(domaine);

        return new AddDomaineResponse(savedDomaine.getIdDomaine(), "Domaine créé avec succès !");
    }
}

