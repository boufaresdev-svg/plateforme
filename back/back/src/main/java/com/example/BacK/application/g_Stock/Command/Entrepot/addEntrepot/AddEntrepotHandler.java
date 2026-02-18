package com.example.BacK.application.g_Stock.Command.entrepot.addEntrepot;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Entrepot;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddEntrepotHandler")
public class AddEntrepotHandler implements RequestHandler<AddEntrepotCommand, AddEntrepotResponse> {

    private final IEntrepotRepositoryService entrepotRepositoryService;
    private final ModelMapper modelMapper;

    public AddEntrepotHandler(IEntrepotRepositoryService entrepotRepositoryService, ModelMapper modelMapper) {
        this.entrepotRepositoryService = entrepotRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddEntrepotResponse handle(AddEntrepotCommand command) {
        Entrepot entrepot = new Entrepot();
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
        entrepot.setCapaciteUtilisee(0.0);
        entrepot.setStatut(command.getStatut() != null ? command.getStatut() : "DISPONIBLE");
        entrepot.setEstActif(command.getEstActif() != null ? command.getEstActif() : true);
        
        Entrepot savedEntrepot = entrepotRepositoryService.save(entrepot);
        
        return modelMapper.map(savedEntrepot, AddEntrepotResponse.class);
    }
}
