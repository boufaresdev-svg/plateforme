package com.example.BacK.application.g_Formation.Command.Domaine.deleteDomaine;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.DomaineRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteDomaineHandler")
public class DeleteDomaineHandler implements RequestHandler<DeleteDomaineCommand, Void> {

    private final DomaineRepositoryService domaineRepositoryService;

    public DeleteDomaineHandler(DomaineRepositoryService domaineRepositoryService) {
        this.domaineRepositoryService = domaineRepositoryService;
    }

    @Override
    public Void handle(DeleteDomaineCommand command) {
        if (domaineRepositoryService.getDomaineById(command.getIdDomaine()).isEmpty()) {
            throw new IllegalArgumentException("Domaine non trouvé avec l'ID : " + command.getIdDomaine());
        }

        domaineRepositoryService.deleteDomaine(command.getIdDomaine());

        return null;
    }
}
