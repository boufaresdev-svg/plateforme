package com.example.BacK.application.g_Formation.Command.Domaine.updateDomaine;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Domaine;
import com.example.BacK.infrastructure.services.g_Formation.DomaineRepositoryService;
import org.springframework.stereotype.Component;

@Component("UpdateDomaineHandler")
public class UpdateDomaineHandler implements RequestHandler<UpdateDomaineCommand, Void> {

    private final DomaineRepositoryService domaineRepositoryService;

    public UpdateDomaineHandler(DomaineRepositoryService domaineRepositoryService) {
        this.domaineRepositoryService = domaineRepositoryService;
    }

    @Override
    public Void handle(UpdateDomaineCommand command) {
        Domaine domaine = domaineRepositoryService.getDomaineById(command.getIdDomaine())
                .orElseThrow(() -> new IllegalArgumentException("Domaine non trouvé avec l'ID : " + command.getIdDomaine()));

        domaine.setNom(command.getNom());
        domaine.setDescription(command.getDescription());

        domaineRepositoryService.saveDomaine(domaine);

        return null;
    }
}

