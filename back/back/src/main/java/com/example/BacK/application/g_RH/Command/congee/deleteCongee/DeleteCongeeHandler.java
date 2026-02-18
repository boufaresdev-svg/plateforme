package com.example.BacK.application.g_RH.Command.congee.deleteCongee;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_rh.CongeeRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteCongeeHandler")
public class DeleteCongeeHandler implements RequestHandler<DeleteCongeeCommand, Void> {

    private final CongeeRepositoryService congeeRepositoryService;

    public DeleteCongeeHandler(CongeeRepositoryService congeeRepositoryService) {
        this.congeeRepositoryService = congeeRepositoryService;
    }

    @Override
    public Void handle(DeleteCongeeCommand command) {
        this.congeeRepositoryService.delete(command.getId());
        return null;
    }
}