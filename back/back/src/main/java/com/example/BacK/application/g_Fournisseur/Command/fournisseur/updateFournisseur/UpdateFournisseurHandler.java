package com.example.BacK.application.g_Fournisseur.Command.fournisseur.updateFournisseur;

import com.example.BacK.application.exceptions.FournisseurNotFoundException;
import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import org.springframework.stereotype.Component;

@Component("UpdateFournisseurHandler")
public class UpdateFournisseurHandler implements RequestHandler<UpdateFournisseurCommand, UpdateFournisseurResponse> {

    private final IFournisseurRepositoryService fournisseurRepositoryService;

    public UpdateFournisseurHandler(IFournisseurRepositoryService fournisseurRepositoryService) {
        this.fournisseurRepositoryService = fournisseurRepositoryService;
    }

    @Override
    public UpdateFournisseurResponse handle(UpdateFournisseurCommand command) {
        Fournisseur existingFournisseur = fournisseurRepositoryService.getById(command.getId());
        
        if (existingFournisseur == null) {
            throw FournisseurNotFoundException.withId(command.getId());
        }
        

        updateFournisseurFields(existingFournisseur, command);
        
        this.fournisseurRepositoryService.update(existingFournisseur);
        return new UpdateFournisseurResponse(existingFournisseur.getId());
    }
    
    private void updateFournisseurFields(Fournisseur existingFournisseur, UpdateFournisseurCommand command) {
        if (command.getNom() != null && !command.getNom().trim().isEmpty()) {
            existingFournisseur.setNom(command.getNom().trim());
        }
        
        if (command.getInfoContact() != null) {
            existingFournisseur.setInfoContact(command.getInfoContact().trim().isEmpty() ? null : command.getInfoContact().trim());
        }
        
        if (command.getAdresse() != null) {
            existingFournisseur.setAdresse(command.getAdresse().trim().isEmpty() ? null : command.getAdresse().trim());
        }
        
        if (command.getTelephone() != null) {
            existingFournisseur.setTelephone(command.getTelephone().trim().isEmpty() ? null : command.getTelephone().trim());
        }
        
        if (command.getMatriculeFiscale() != null) {
            existingFournisseur.setMatriculeFiscale(command.getMatriculeFiscale().trim().isEmpty() ? null : command.getMatriculeFiscale().trim());
        }
    }
}