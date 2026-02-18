package com.example.BacK.application.g_Vehicule.Command.vehicule.deleteVehicule;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IVehiculeRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;

import org.springframework.stereotype.Component;

@Component("DeleteVehiculeHandler")
public class DeleteVehiculeHandler implements RequestHandler<DeleteVehiculeCommand,Void> {

    private  final IVehiculeRepositoryService vehiculeRepositoryService;

    public DeleteVehiculeHandler(IVehiculeRepositoryService vehiculeRepositoryService) {
        this.vehiculeRepositoryService = vehiculeRepositoryService;
    }

    @Override
    public Void handle(DeleteVehiculeCommand command) {
        this.vehiculeRepositoryService.delete(command.getId());
        return null;
    }

}
