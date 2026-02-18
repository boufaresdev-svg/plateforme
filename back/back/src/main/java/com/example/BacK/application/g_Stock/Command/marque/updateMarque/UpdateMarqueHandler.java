package com.example.BacK.application.g_Stock.Command.marque.updateMarque;

import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Marque;
import org.springframework.stereotype.Component;

@Component("UpdateMarqueHandler")
public class UpdateMarqueHandler implements RequestHandler<UpdateMarqueCommand, UpdateMarqueResponse> {

    private final IMarqueRepositoryService marqueRepositoryService;

    public UpdateMarqueHandler(IMarqueRepositoryService marqueRepositoryService) {
        this.marqueRepositoryService = marqueRepositoryService;
    }

    @Override
    public UpdateMarqueResponse handle(UpdateMarqueCommand command) {
        Marque existingMarque = marqueRepositoryService.getById(command.getId());
        if (existingMarque == null) {
            throw new IllegalArgumentException("Marque introuvable avec l'ID : " + command.getId());
        }

        // Update fields
        existingMarque.setNom(command.getNom());
        existingMarque.setCodeMarque(command.getCodeMarque());
        existingMarque.setDescription(command.getDescription());
        existingMarque.setPays(command.getPays());
        existingMarque.setSiteWeb(command.getSiteWeb());
        existingMarque.setUrlLogo(command.getUrlLogo());
        existingMarque.setNomContact(command.getNomContact());
        existingMarque.setEmail(command.getEmail());
        existingMarque.setTelephone(command.getTelephone());
        existingMarque.setPoste(command.getPoste());
        
        if (command.getEstActif() != null) {
            existingMarque.setEstActif(command.getEstActif());
        }

        marqueRepositoryService.update(existingMarque);
        return new UpdateMarqueResponse(command.getId());
    }
}
