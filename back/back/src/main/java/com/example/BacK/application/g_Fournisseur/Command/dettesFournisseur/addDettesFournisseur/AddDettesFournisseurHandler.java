package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.addDettesFournisseur;

import com.example.BacK.application.exceptions.FournisseurNotFoundException;
import com.example.BacK.application.exceptions.DuplicateNumeroFactureException;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddDettesFournisseurHandler")
public class AddDettesFournisseurHandler implements RequestHandler<AddDettesFournisseurCommand, AddDettesFournisseurResponse> {

    private final IDettesFournisseurRepositoryService dettesFournisseurRepositoryService;
    private final IFournisseurRepositoryService fournisseurRepositoryService;
    private final ModelMapper modelMapper;

    public AddDettesFournisseurHandler(IDettesFournisseurRepositoryService dettesFournisseurRepositoryService,
                                       IFournisseurRepositoryService fournisseurRepositoryService,
                                       ModelMapper modelMapper) {
        this.dettesFournisseurRepositoryService = dettesFournisseurRepositoryService;
        this.fournisseurRepositoryService = fournisseurRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddDettesFournisseurResponse handle(AddDettesFournisseurCommand command) {
        
        if (command.getNumeroFacture() != null && 
            dettesFournisseurRepositoryService.existsByNumeroFacture(command.getNumeroFacture())) {
            throw DuplicateNumeroFactureException.withNumeroFacture(command.getNumeroFacture());
        }
        
        DettesFournisseur dettesFournisseur = modelMapper.map(command, DettesFournisseur.class);
        

        if (command.getFournisseurId() != null) {
            Fournisseur fournisseur = fournisseurRepositoryService.getById(command.getFournisseurId());
            if (fournisseur == null) {
                throw FournisseurNotFoundException.withId(command.getFournisseurId());
            }
            dettesFournisseur.setFournisseur(fournisseur);
        }
        
        String id = this.dettesFournisseurRepositoryService.add(dettesFournisseur);
        return new AddDettesFournisseurResponse(id);
    }
}