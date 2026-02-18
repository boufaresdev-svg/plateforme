package com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formateur;
import com.example.BacK.infrastructure.services.g_Formation.FormateurRepositoryService;
import org.springframework.stereotype.Component;

@Component("UpdateFormateurHandler")
public class UpdateFormateurHandler implements RequestHandler<UpdateFormateurCommand, Void> {

    private final FormateurRepositoryService formateurRepositoryService;

    public UpdateFormateurHandler(FormateurRepositoryService formateurRepositoryService) {
        this.formateurRepositoryService = formateurRepositoryService;
    }

    @Override
    public Void handle(UpdateFormateurCommand command) {

        if (command.getIdFormateur() == null) {
            throw new IllegalArgumentException("L'ID du formateur ne peut pas être nul.");
        }

        Formateur formateur = formateurRepositoryService
                .getFormateurById(command.getIdFormateur())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formateur non trouvé avec l'ID : " + command.getIdFormateur()
                ));

        formateur.setNom(command.getNom());
        formateur.setPrenom(command.getPrenom());
        formateur.setSpecialite(command.getSpecialite());
        formateur.setContact(command.getContact());
        formateur.setExperience(command.getExperience());

        formateurRepositoryService.saveFormateur(formateur);
        return null;
    }
}
