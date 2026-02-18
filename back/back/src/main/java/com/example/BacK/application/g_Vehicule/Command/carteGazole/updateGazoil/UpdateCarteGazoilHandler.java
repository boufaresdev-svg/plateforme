package com.example.BacK.application.g_Vehicule.Command.carteGazole.updateGazoil;

 import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.CarteGazoil;
import com.example.BacK.infrastructure.services.g_Vehicule.CarteGazoilRepositoryService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateCarteGazoilHandler")
public class UpdateCarteGazoilHandler implements RequestHandler<UpdateCarteGazoilCommand, Void> {

    private final CarteGazoilRepositoryService carteGazoilRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateCarteGazoilHandler(CarteGazoilRepositoryService carteGazoilRepositoryService, ModelMapper modelMapper) {
        this.carteGazoilRepositoryService = carteGazoilRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateCarteGazoilCommand command) {
        CarteGazoil existingEntity = this.carteGazoilRepositoryService.get(command.getId());
        if (existingEntity == null) {
            throw new EntityNotFoundException("Entity CarteGazoil not found");
        }

        this.modelMapper.map(command, existingEntity);
        this.carteGazoilRepositoryService.update(existingEntity);
        return null;
    }
}
