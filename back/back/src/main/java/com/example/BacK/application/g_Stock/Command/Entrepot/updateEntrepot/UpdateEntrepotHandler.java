package com.example.BacK.application.g_Stock.Command.entrepot.updateEntrepot;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Entrepot;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateEntrepotHandler")
public class UpdateEntrepotHandler implements RequestHandler<UpdateEntrepotCommand, UpdateEntrepotResponse> {
    
    private final IEntrepotRepositoryService entrepotRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateEntrepotHandler(IEntrepotRepositoryService entrepotRepositoryService, ModelMapper modelMapper) {
        this.entrepotRepositoryService = entrepotRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public UpdateEntrepotResponse handle(UpdateEntrepotCommand command) {
        Entrepot entrepot = entrepotRepositoryService.findById(command.getId())
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + command.getId()));

        entrepot.setNom(command.getNom());
        entrepot.setDescription(command.getDescription());
        entrepot.setAdresse(command.getAdresse());
        entrepot.setVille(command.getVille());
        entrepot.setCodePostal(command.getCodePostal());
        entrepot.setTelephone(command.getTelephone());
        entrepot.setEmail(command.getEmail());
        entrepot.setResponsable(command.getResponsable());
        entrepot.setSuperficie(command.getSuperficie());
        entrepot.setCapaciteMaximale(command.getCapaciteMaximale());
        entrepot.setStatut(command.getStatut());
        entrepot.setEstActif(command.getEstActif());

        Entrepot updatedEntrepot = entrepotRepositoryService.save(entrepot);

        return modelMapper.map(updatedEntrepot, UpdateEntrepotResponse.class);
    }
}
