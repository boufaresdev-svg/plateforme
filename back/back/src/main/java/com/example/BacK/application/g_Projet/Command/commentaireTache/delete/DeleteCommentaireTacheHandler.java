package com.example.BacK.application.g_Projet.Command.commentaireTache.delete;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.CommentaireTacheRepositoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component("DeleteCommentaireTacheHandler")
public class DeleteCommentaireTacheHandler implements RequestHandler<DeleteCommentaireTacheCommand, Void> {

    private final CommentaireTacheRepositoryService commentaireTacheRepositoryService;

    public DeleteCommentaireTacheHandler(CommentaireTacheRepositoryService commentaireTacheRepositoryService) {
        this.commentaireTacheRepositoryService = commentaireTacheRepositoryService;
    }

    @Override
    public Void handle(DeleteCommentaireTacheCommand command) {
        commentaireTacheRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}
