package com.example.BacK.application.g_Vehicule.Command.reparation.addReparation;


import com.example.BacK.application.interfaces.g_Vehicule.Reparation.IReparationRepositoryService;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IVehiculeRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.Reparation;
import com.example.BacK.domain.g_Vehicule.Vehicule;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddReparationHandler")
public class AddReparationHandler implements RequestHandler<AddReparationCommand, AddReparationResponse> {

    private final IReparationRepositoryService reparationRepositoryService;
    private final IVehiculeRepositoryService vehiculeRepositoryService;
    private final ModelMapper modelMapper;

    public AddReparationHandler(IReparationRepositoryService reparationRepositoryService, IVehiculeRepositoryService vehiculeRepositoryService, ModelMapper modelMapper) {
        this.reparationRepositoryService = reparationRepositoryService;
        this.vehiculeRepositoryService = vehiculeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddReparationResponse handle(AddReparationCommand command) {
        Reparation reparation = modelMapper.map(command, Reparation.class);
        Vehicule foundVehicule = vehiculeRepositoryService.get(command.getVehiculeId());
        reparation.setId(null);
        reparation.setVehicule(foundVehicule);
        String id = this.reparationRepositoryService.add(reparation);
        return new AddReparationResponse(id);
    }
}