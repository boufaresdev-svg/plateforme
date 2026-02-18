package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.deleteDettesFournisseur;

import com.example.BacK.application.exceptions.DettesFournisseurNotFoundException;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import org.springframework.stereotype.Component;

@Component("DeleteDettesFournisseurHandler")
public class DeleteDettesFournisseurHandler implements RequestHandler<DeleteDettesFournisseurCommand, DeleteDettesFournisseurResponse> {

    private final IDettesFournisseurRepositoryService dettesFournisseurRepositoryService;

    public DeleteDettesFournisseurHandler(IDettesFournisseurRepositoryService dettesFournisseurRepositoryService) {
        this.dettesFournisseurRepositoryService = dettesFournisseurRepositoryService;
    }

    @Override
    public DeleteDettesFournisseurResponse handle(DeleteDettesFournisseurCommand command) {
        DettesFournisseur existingDette = dettesFournisseurRepositoryService.getById(command.getId());
        
        if (existingDette == null) {
            throw DettesFournisseurNotFoundException.withId(command.getId());
        }
        
        this.dettesFournisseurRepositoryService.delete(command.getId());
        return new DeleteDettesFournisseurResponse(command.getId());
    }
}