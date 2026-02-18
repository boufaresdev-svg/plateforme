package com.example.BacK.application.g_Projet.Command.ProblemeTache.delete;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.ProblemeTacheRepositoryService;
import org.springframework.stereotype.Component;


@Component("DeleteProblemeTacheHandler")
public class DeleteProblemeTacheHandler implements RequestHandler<DeleteProblemeTacheCommand, Void> {

    private final ProblemeTacheRepositoryService problemeTacheRepositoryService;

    public DeleteProblemeTacheHandler(ProblemeTacheRepositoryService problemeTacheRepositoryService) {
        this.problemeTacheRepositoryService = problemeTacheRepositoryService;
    }

    @Override
    public Void handle(DeleteProblemeTacheCommand command) {
        problemeTacheRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}