package com.example.BacK.application.g_Fournisseur.Command.fournisseur.deleteFournisseur;

import com.example.BacK.application.exceptions.FournisseurHasDettesException;
import com.example.BacK.application.exceptions.FournisseurNotFoundException;
import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import org.springframework.stereotype.Component;

@Component("DeleteFournisseurHandler")
public class DeleteFournisseurHandler implements RequestHandler<DeleteFournisseurCommand, DeleteFournisseurResponse> {

    private final IFournisseurRepositoryService fournisseurRepositoryService;

    public DeleteFournisseurHandler(IFournisseurRepositoryService fournisseurRepositoryService) {
        this.fournisseurRepositoryService = fournisseurRepositoryService;
    }

    @Override
    public DeleteFournisseurResponse handle(DeleteFournisseurCommand command) {
        Fournisseur existingFournisseur = fournisseurRepositoryService.getById(command.getId());
        
        if (existingFournisseur == null) {
            throw FournisseurNotFoundException.withId(command.getId());
        }
        
        if (existingFournisseur.getDettes() != null && !existingFournisseur.getDettes().isEmpty()) {
            throw FournisseurHasDettesException.withId(command.getId());
        }
        
        this.fournisseurRepositoryService.delete(command.getId());
        return new DeleteFournisseurResponse(command.getId());
    }
}