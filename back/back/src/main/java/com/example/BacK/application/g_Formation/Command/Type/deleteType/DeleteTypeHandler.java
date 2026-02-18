package com.example.BacK.application.g_Formation.Command.Type.deleteType;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.TypeRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteTypeHandler")
public class DeleteTypeHandler implements RequestHandler<DeleteTypeCommand, Void> {

    private final TypeRepositoryService typeRepositoryService;

    public DeleteTypeHandler(TypeRepositoryService typeRepositoryService) {
        this.typeRepositoryService = typeRepositoryService;
    }

    @Override
    public Void handle(DeleteTypeCommand command) {
        if (!typeRepositoryService.existsById(command.getIdType())) {
            throw new IllegalArgumentException("Type non trouvé avec l'ID : " + command.getIdType());
        }

        typeRepositoryService.deleteType(command.getIdType());

        return null;
    }
}

