package com.example.BacK.application.g_RH.Command.congee.updateCongee;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_RH.Congee;
import com.example.BacK.infrastructure.services.g_rh.CongeeRepositoryService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateCongeeHandler")
public class UpdateCongeeHandler implements RequestHandler<UpdateCongeeCommand, Void> {

    private final CongeeRepositoryService  congeeRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateCongeeHandler(CongeeRepositoryService congeeRepositoryService, ModelMapper modelMapper) {
        this.congeeRepositoryService = congeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateCongeeCommand command) {
        Congee existingEntity = this.congeeRepositoryService.get(command.getId());
        if (existingEntity == null) {
            throw new EntityNotFoundException("Entity Congee not found");
        }
        this.modelMapper.map(command, existingEntity);
        this.congeeRepositoryService.update(existingEntity);
        return null;
    }
}
