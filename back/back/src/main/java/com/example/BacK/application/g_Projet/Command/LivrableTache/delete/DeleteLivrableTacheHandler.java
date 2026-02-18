package com.example.BacK.application.g_Projet.Command.LivrableTache.delete;

import com.example.BacK.application.g_Projet.Command.commentaireTache.delete.DeleteCommentaireTacheCommand;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.LivrableTacheRepositoryService;
import org.springframework.stereotype.Component;


@Component("DeleteLivrableTacheHandler")
public class DeleteLivrableTacheHandler implements RequestHandler<DeleteLivrableTacheCommand, Void> {

    private final LivrableTacheRepositoryService livrableTacheRepositoryService;

    public DeleteLivrableTacheHandler(LivrableTacheRepositoryService livrableTacheRepositoryService) {
        this.livrableTacheRepositoryService = livrableTacheRepositoryService;
    }

    @Override
    public Void handle(DeleteLivrableTacheCommand command) {
        livrableTacheRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}
