package com.example.BacK.application.g_Projet.Command.commentaireTache.add;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.CommentaireTache;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.CommentaireTacheRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddCommentaireTacheHandler")
public class AddCommentaireTacheHandler implements RequestHandler<AddCommentaireTacheCommand, AddCommentaireTacheResponse> {

    private final CommentaireTacheRepositoryService commentaireTacheRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final TacheRepositoryService tacheRepositoryService;
    private final ModelMapper modelMapper;

    public AddCommentaireTacheHandler(CommentaireTacheRepositoryService commentaireTacheRepositoryService,
                                      EmployeeRepositoryService employeeRepositoryService,
                                      TacheRepositoryService tacheRepositoryService,
                                      ModelMapper modelMapper) {
        this.commentaireTacheRepositoryService = commentaireTacheRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.tacheRepositoryService = tacheRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCommentaireTacheResponse handle(AddCommentaireTacheCommand command) {
        CommentaireTache commentaire = modelMapper.map(command, CommentaireTache.class);

        // Récupération de la tâche
        Tache tacheFound = tacheRepositoryService.get(command.getTacheId());
        commentaire.setTache(tacheFound);

        // Ajout du commentaire
        String id = commentaireTacheRepositoryService.add(commentaire);
        return new AddCommentaireTacheResponse(id);
    }
}
