package com.example.BacK.application.g_Fournisseur.Command.fournisseur.addFournisseur;

import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddFournisseurHandler")
public class AddFournisseurHandler implements RequestHandler<AddFournisseurCommand, AddFournisseurResponse> {

    private final IFournisseurRepositoryService fournisseurRepositoryService;
    private final ModelMapper modelMapper;

    public AddFournisseurHandler(IFournisseurRepositoryService fournisseurRepositoryService, ModelMapper modelMapper) {
        this.fournisseurRepositoryService = fournisseurRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddFournisseurResponse handle(AddFournisseurCommand command) {
        Fournisseur fournisseur = modelMapper.map(command, Fournisseur.class);
        String id = this.fournisseurRepositoryService.add(fournisseur);
        return new AddFournisseurResponse(id);
    }
}