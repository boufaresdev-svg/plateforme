package com.example.BacK.application.g_Vehicule.Command.carteTelepeage.updateCarteTelepeage;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Vehicule.CarteTelepeage;
import com.example.BacK.infrastructure.services.g_Vehicule.CarteTelepeageRepositoryService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateCarteTelepeageHandler")
public class UpdateCarteTelepeageHandler implements RequestHandler<UpdateCarteTelepeageCommand, Void> {

    private final ModelMapper modelMapper;
    private final CarteTelepeageRepositoryService carteTelepeageRepositoryService;

    public UpdateCarteTelepeageHandler(ModelMapper modelMapper, CarteTelepeageRepositoryService carteTelepeageRepositoryService) {
        this.modelMapper = modelMapper;
        this.carteTelepeageRepositoryService = carteTelepeageRepositoryService;
    }

    @Override
    public Void handle(UpdateCarteTelepeageCommand command) {
        CarteTelepeage existingEntity = this.carteTelepeageRepositoryService.get(command.getId());
        if (existingEntity == null) {
            throw new EntityNotFoundException("Entity CarteTelepeage not found");
        }

        this.modelMapper.map(command, existingEntity);
        this.carteTelepeageRepositoryService.update(existingEntity);
        return null;
    }
}
