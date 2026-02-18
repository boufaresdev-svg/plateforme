package com.example.BacK.application.g_Projet.Command.LivrableTache.add;

import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheCommand;
import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheResponse;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.CommentaireTache;
import com.example.BacK.domain.g_Projet.LivrableTache;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.infrastructure.services.g_Projet.CommentaireTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.LivrableTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("AddLivrableTacheHandler")
public class AddLivrableTacheHandler implements RequestHandler<AddLivrableTacheCommand, AddLivrableTacheResponse> {

    private final LivrableTacheRepositoryService livrableTacheRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final TacheRepositoryService tacheRepositoryService;
    private final ModelMapper modelMapper;

    public AddLivrableTacheHandler(LivrableTacheRepositoryService livrableTacheRepositoryService, EmployeeRepositoryService employeeRepositoryService, TacheRepositoryService tacheRepositoryService, ModelMapper modelMapper) {
        this.livrableTacheRepositoryService = livrableTacheRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.tacheRepositoryService = tacheRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddLivrableTacheResponse handle(AddLivrableTacheCommand command) {
        LivrableTache livrableTache = modelMapper.map(command, LivrableTache.class);

        // Récupération de la tâche
        Tache tacheFound = tacheRepositoryService.get(command.getTacheId());
        livrableTache.setTache(tacheFound);

        // Ajout du commentaire
        String id = livrableTacheRepositoryService.add(livrableTache);
        return new AddLivrableTacheResponse(id);
    }
}