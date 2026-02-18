package com.example.BacK.application.g_Projet.Command.ProblemeTache.update;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.ProblemeTache;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.infrastructure.services.g_Projet.ProblemeTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("UpdateProblemeTacheHandler")
public class UpdateProblemeTacheHandler implements RequestHandler<UpdateProblemeTacheCommand, Void> {

    private final ProblemeTacheRepositoryService problemeTacheRepositoryService;

    private final TacheRepositoryService tacheRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateProblemeTacheHandler(ProblemeTacheRepositoryService problemeTacheRepositoryService, TacheRepositoryService tacheRepositoryService, ModelMapper modelMapper) {
        this.problemeTacheRepositoryService = problemeTacheRepositoryService;
        this.tacheRepositoryService = tacheRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateProblemeTacheCommand command) {
        ProblemeTache problemeTache = modelMapper.map(command, ProblemeTache.class);

        // Récupération de la tâche
        Tache tacheFound = tacheRepositoryService.get(command.getTacheId());
        problemeTache.setTache(tacheFound);

        // Mise à jour du commentaire
        problemeTacheRepositoryService.update(problemeTache);

        return null; // retour Void
    }
}
