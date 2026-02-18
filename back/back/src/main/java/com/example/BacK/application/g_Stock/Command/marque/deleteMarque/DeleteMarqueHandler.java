package com.example.BacK.application.g_Stock.Command.marque.deleteMarque;

import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteMarqueHandler")
public class DeleteMarqueHandler implements RequestHandler<DeleteMarqueCommand, DeleteMarqueResponse> {

    private final IMarqueRepositoryService marqueRepositoryService;

    public DeleteMarqueHandler(IMarqueRepositoryService marqueRepositoryService) {
        this.marqueRepositoryService = marqueRepositoryService;
    }

    @Override
    public DeleteMarqueResponse handle(DeleteMarqueCommand command) {
        marqueRepositoryService.delete(command.getId());
        return new DeleteMarqueResponse(command.getId());
    }
}
