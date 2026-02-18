package com.example.BacK.application.g_Vehicule.Command.vehicule.addVehicule;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IVehiculeRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.Vehicule;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddVehiculeHandler")
public class AddVehiculeHandler implements RequestHandler<AddVehiculeCommand, AddVehiculeResponse> {
    private  final IVehiculeRepositoryService _vehiculeRepositoryService;
    private final ModelMapper _modelMapper;

    public AddVehiculeHandler(IVehiculeRepositoryService _vehiculeRepositoryService, ModelMapper _modelMapper) {
        this._vehiculeRepositoryService = _vehiculeRepositoryService;
        this._modelMapper = _modelMapper;
    }

    @Override
    public AddVehiculeResponse handle(AddVehiculeCommand command) {
        Vehicule vehicule = _modelMapper.map(command,Vehicule.class);
        String id = this._vehiculeRepositoryService.add(vehicule);
        return   new AddVehiculeResponse(id );
    }
}
