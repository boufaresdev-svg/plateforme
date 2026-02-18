package com.example.BacK.application.g_Projet.Command.LivrableTache.update;

import com.example.BacK.application.g_Projet.Command.commentaireTache.update.UpdateCommentaireTacheCommand;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.CommentaireTache;
import com.example.BacK.domain.g_Projet.LivrableTache;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.infrastructure.services.g_Projet.CommentaireTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.LivrableTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;



@Component("UpdateLivrableTacheHandler")
public class UpdateLivrableTacheHandler implements RequestHandler<UpdateLivrableTacheCommand, Void> {

    private final LivrableTacheRepositoryService livrableTacheRepositoryService;

    private final TacheRepositoryService tacheRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateLivrableTacheHandler(LivrableTacheRepositoryService livrableTacheRepositoryService, TacheRepositoryService tacheRepositoryService, ModelMapper modelMapper) {
        this.livrableTacheRepositoryService = livrableTacheRepositoryService;
        this.tacheRepositoryService = tacheRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateLivrableTacheCommand command) {
        LivrableTache livrableTache = modelMapper.map(command, LivrableTache.class);

        // Récupération de la tâche
        Tache tacheFound = tacheRepositoryService.get(command.getTacheId());
        livrableTache.setTache(tacheFound);

        // Mise à jour du commentaire
        livrableTacheRepositoryService.update(livrableTache);

        return null; // retour Void
    }
}

