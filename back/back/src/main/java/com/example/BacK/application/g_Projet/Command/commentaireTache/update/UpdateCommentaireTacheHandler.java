package com.example.BacK.application.g_Projet.Command.commentaireTache.update;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.CommentaireTache;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.CommentaireTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateCommentaireTacheHandler")
public class UpdateCommentaireTacheHandler implements RequestHandler<UpdateCommentaireTacheCommand, Void> {

    private final CommentaireTacheRepositoryService commentaireTacheRepositoryService;

    private final TacheRepositoryService tacheRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateCommentaireTacheHandler(CommentaireTacheRepositoryService commentaireTacheRepositoryService,
                                         TacheRepositoryService tacheRepositoryService,
                                         ModelMapper modelMapper) {
        this.commentaireTacheRepositoryService = commentaireTacheRepositoryService;

        this.tacheRepositoryService = tacheRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateCommentaireTacheCommand command) {
        CommentaireTache commentaire = modelMapper.map(command, CommentaireTache.class);

        // Récupération de la tâche
        Tache tacheFound = tacheRepositoryService.get(command.getTache());
        commentaire.setTache(tacheFound);

        // Mise à jour du commentaire
        commentaireTacheRepositoryService.update(commentaire);

        return null; // retour Void
    }
}

