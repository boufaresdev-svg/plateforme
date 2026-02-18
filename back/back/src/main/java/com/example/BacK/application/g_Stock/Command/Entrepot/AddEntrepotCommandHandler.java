package com.example.BacK.application.g_Stock.Command.Entrepot;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Entrepot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AddEntrepotCommandHandler implements RequestHandler<AddEntrepotCommand, AddEntrepotCommandResponse> {
    
    private final IEntrepotRepositoryService repositoryService;
    
    @Override
    public AddEntrepotCommandResponse handle(AddEntrepotCommand command) {
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
        
        Entrepot savedEntrepot = repositoryService.save(entrepot);
        
        return new AddEntrepotCommandResponse(
            savedEntrepot.getId(),
            savedEntrepot.getNom(),
            savedEntrepot.getDescription(),
            savedEntrepot.getAdresse(),
            savedEntrepot.getVille(),
            savedEntrepot.getCodePostal(),
            savedEntrepot.getTelephone(),
            savedEntrepot.getEmail(),
            savedEntrepot.getResponsable(),
            savedEntrepot.getSuperficie(),
            savedEntrepot.getCapaciteMaximale(),
            savedEntrepot.getCapaciteUtilisee(),
            savedEntrepot.getStatut(),
            savedEntrepot.getEstActif(),
            savedEntrepot.getCreatedAt(),
            savedEntrepot.getUpdatedAt()
        );
    }
}
