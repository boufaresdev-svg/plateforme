package com.example.BacK.application.g_Vehicule.Command.vehicule.UpdateVehicule;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IVehiculeRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.Vehicule;
import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateAtelierHandler")
public class UpdateVehiculeHandler implements RequestHandler<UpdateVehiculeCommand,Void> {

    private  final IVehiculeRepositoryService _VehiculeRepositoryService;
    private final ModelMapper _modelMapper;

    public UpdateVehiculeHandler(IVehiculeRepositoryService _VehiculeRepositoryService, ModelMapper _modelMapper) {
        this._VehiculeRepositoryService = _VehiculeRepositoryService;
        this._modelMapper = _modelMapper;
    }

    @Override
    public Void handle(UpdateVehiculeCommand command) {
        Vehicule existingEntity = this._VehiculeRepositoryService.get(command.getId());
        if(existingEntity == null)
        {
            throw new EntityNotFoundException("Entity Vehicule  not found");
        }

        this._modelMapper.map(command, existingEntity);
        this._VehiculeRepositoryService.update(existingEntity);
        return null;
    }
}
